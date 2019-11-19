package br.com.itau.modernizacao.kafka.saldo;


import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import br.com.itau.modernizacao.tracer.KafkaService;
import br.com.itau.modernizacao.tracer.TracerContextClientApplication;

import io.opentracing.Span;



@Service
public class SaldoService extends TracerContextClientApplication {

	
	private static final Logger logger = LoggerFactory.getLogger(SaldoService.class);

	
	
    @Autowired
    KafkaTemplate<String, Message> kafkaTemplate;

	KafkaService kafka = new KafkaService();
	
	@KafkaListener(topics="simulacao", groupId = "saldo-groupid" )
	public void validaSaldo(String mensagem, @Headers MessageHeaders headers)
	{
		logger.info("Valida saldo pelo Kafka, mensagem " + mensagem);
		Span span = kafka.startConsumerSpan("validaSaldo", headers, tracer);

		try
		{	
			//Scope scope = tracer.scopeManager().activate(span,false);
			logger.info("Vai processar a validacao do saldo com base na mensagem do kafka: " + mensagem);
			String[] msg = mensagem.split(";");
			int agenciaOrigem = Integer.parseInt(msg[0]);
			int contaOrigem = Integer.parseInt(msg[1]);
			double valor = Double.parseDouble(msg[4]);
			
			RestTemplate client = new RestTemplate();
			URI uri = new URI("http://contacorrente-svc:9002/contas/agencia/" + agenciaOrigem +"_" + contaOrigem);
			
			logger.info("Vai invocar o serviço que recupera os detalhes incluindo o saldo da conta");
			ContaCorrente resposta = this.get(span, "get-conta-Saldo",uri,ContaCorrente.class, client); //client.getForEntity(uri,ContaCorrente.class);

			if (resposta.getSaldo()<valor)
			{
				logger.info("Saldo insuficiente, saldo atual " + resposta.getSaldo() + ", valor da transferencia: " + valor);
				span.log("Saldo insuficente");
				kafka.sendMessage("false", span, "saldo", tracer, kafkaTemplate, "respostaSaldo");
			}
			else
			{
				logger.info("Saldo suficiente!");
				kafka.sendMessage("true", span, "saldo", tracer, kafkaTemplate, "respostaSaldo");
			}
			span.setTag("error", false);
		}
		catch (Exception e)
		{
			logger.error("Falha ao processar Saldo: " + e.getMessage());
			span.setTag("error", true);
			span.setTag("Error Message", e.getMessage());
			e.printStackTrace();
			kafka.sendMessage("false", span, "saldo", tracer, kafkaTemplate, "respostaSaldo");
		}
		finally
		{
			span.finish();
		}
	}
	
}
