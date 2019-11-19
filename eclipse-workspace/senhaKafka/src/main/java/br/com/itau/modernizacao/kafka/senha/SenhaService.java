package br.com.itau.modernizacao.kafka.senha;

import java.net.URI;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.itau.modernizacao.tracer.KafkaService;
import br.com.itau.modernizacao.tracer.TracerContextClientApplication;
import io.opentracing.Span;
import io.opentracing.Tracer;

@Service
public class SenhaService extends TracerContextClientApplication {

	private static final Logger logger = LoggerFactory.getLogger(SenhaService.class);
	
	
	
	private Tracer tracer;
	
    @Autowired
    KafkaTemplate<String, Message> kafkaTemplate;

	KafkaService kafka = new KafkaService();

	@Autowired
	public SenhaService(Tracer tracer)
	{
		this.tracer = tracer;
	}
	
	@KafkaListener(topics="simulacao", groupId = "senha-groupid")
	public void validaSenha(String mensagem, @Headers MessageHeaders headers)
	{
		logger.info("Valida senha pelo Kafka, mensagem " + mensagem);
		Span span = kafka.startConsumerSpan("processaSenha", headers, tracer);
		try
		{
			logger.debug("fazendo parse da mensagem: " + mensagem);
			//Scope scope = tracer.scopeManager().activate(span,false);

			String[] msg = mensagem.split(";");
			int agenciaOrigem = Integer.parseInt(msg[0]);
			int contaOrigem = Integer.parseInt(msg[1]);
			String senha = msg[5];
			logger.debug("Parse da mensagem, identificando agencia: " + agenciaOrigem + " e conta: " + contaOrigem);
			RestTemplate client = new RestTemplate();
			
			logger.info("Vai chamar o serviço de conta corrente para verificar se a conta existe, e recupear o Id da conta");
			String url = "http://contacorrente-svc:9002/contas/agencia/" + agenciaOrigem +"_" + contaOrigem;
	        URI uri = UriComponentsBuilder.fromHttpUrl(url).build(Collections.emptyMap());
	        ContaCorrente resposta = this.get(span,"get-Conta-Senha", uri, br.com.itau.modernizacao.kafka.senha.ContaCorrente.class, client);

			ValidaSenhaDTO dados = new ValidaSenhaDTO();
			dados.setContaID(resposta.getIdConta());
			dados.setSenha(senha);
			logger.debug("Vai invocar o servico para validar a senha para a conta com id: " + dados.getContaID());
			ResponseEntity<Boolean> validada = client.postForEntity("http://senha-svc:9004/senha", dados, Boolean.class);
			
			logger.info("Senha validada, resultado: " + validada.getBody());
			kafka.sendMessage(validada.getBody().toString(), span, "senha", tracer, kafkaTemplate, "respostaSenha");
		}
		catch (Exception e)
		{
			logger.error("Falha ao processar Senha: " + e.getMessage());
			e.printStackTrace();
			kafka.sendMessage("false", span, "senha", tracer, kafkaTemplate, "respostaSenha");
		}
		finally
		{
			span.finish();
		}
	}
	
	
}
