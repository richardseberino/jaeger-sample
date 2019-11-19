package br.com.itau.modernizacao.tef.controller;

import java.net.URI;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import br.com.itau.modernizacao.tef.controller.dto.ResultadoTransferenciaDTO;
import br.com.itau.modernizacao.tef.controller.dto.TransferenciaDTO;
import br.com.itau.modernizacao.tracer.KafkaService;
import br.com.itau.modernizacao.tracer.TracerContextClientApplication;
import io.micrometer.core.annotation.Timed;
import io.opentracing.Span;
import io.opentracing.Tracer;

@RestController
@RequestMapping("/tef")
public class TefController extends TracerContextClientApplication {

	
	private boolean aguardaSenha = true;
	private boolean respostaSenha = false;
	private boolean aguardaSaldo = true;
	private boolean respostaSaldo = false;

	private final Tracer tracer;

	private static final Logger logger = LoggerFactory.getLogger(TefController.class);
	
    private KafkaService kafka = new KafkaService();
	
    @Autowired
    KafkaTemplate<String, Message> kafkaTemplate;

    
	@Autowired
	public TefController(Tracer tracker)
	{
		this.tracer = tracker;
	}
	
	private ResultadoTransferenciaDTO preparaResultado(TransferenciaDTO transferencia, Span spanPai)
	{
		Span span = tracer.buildSpan("tef_preparaResultado").asChildOf(spanPai).start();
		try
		{	
			//Scope scope = tracer.scopeManager().activate(span, true);
		
			ResultadoTransferenciaDTO resultado = new ResultadoTransferenciaDTO();
			resultado.setAgenciaDestino(transferencia.getAgenciaDestino());
			resultado.setAgenciaOrigem(transferencia.getAgenciaOrigem());
			resultado.setContaDestino(transferencia.getContaDestino());
			resultado.setContaOrigem(transferencia.getContaOrigem());
			resultado.setValor(transferencia.getValor());
			
			Map<String, String> mapa = new LinkedHashMap<>();
			mapa.put("agenciaOrigem", transferencia.getAgenciaOrigem()+"");
			mapa.put("agenciaDestino", transferencia.getAgenciaDestino()+"");
			mapa.put("contaOrigem", transferencia.getContaOrigem()+"");
			mapa.put("contaDestino", transferencia.getContaDestino()+"");
			mapa.put("valor", transferencia.getValor()+"");
			
 			span.log(mapa);
			return resultado;
		}
		finally
		{
			span.finish();
		}
	}
	
	/**
	 * método responsavel por formatar a mensagem com os dados de negócio que serão enviadas ao Kafka
	 * @param transferencia DTO com os dados recebidos na RestAPI
	 * @param spanPai Span que iniciou o trace
	 * @return
	 */
	private String formatMessage(TransferenciaDTO transferencia, Span spanPai)
	{
		Span span = tracer.buildSpan("tef_formatMessage").asChildOf(spanPai).start();
		try
		{	
			//Scope scope = tracer.scopeManager().activate(span, true);
			StringBuilder transfer = new StringBuilder();
			transfer.append(transferencia.getAgenciaOrigem());
			transfer.append(";");
			transfer.append(transferencia.getContaOrigem());
			transfer.append(";");
			transfer.append(transferencia.getAgenciaDestino());
			transfer.append(";");
			transfer.append(transferencia.getContaDestino());
			transfer.append(";");
			transfer.append(transferencia.getValor());
			transfer.append(";");
			transfer.append(transferencia.getSenha());
			span.setTag("message", transfer.toString());
			return transfer.toString();
			
		}
		finally
		{
			span.finish();
		}
	}
	
	@PostMapping("/simula")
	@Timed("tef_service")
	public ResponseEntity<ResultadoTransferenciaDTO> simulaTranaferencia(@RequestBody TransferenciaDTO transferencia, HttpServletRequest request)
	{
		Span span = tracer.buildSpan("tef_service").start();
		java.util.Date hoje = new java.util.Date();
		span.setTag("agenciaOrigem", transferencia.getAgenciaOrigem());
		span.setTag("agenciaDestino", transferencia.getAgenciaDestino());
		span.setTag("contaOrigem", transferencia.getContaOrigem());
		span.setTag("contaDestino", transferencia.getContaDestino());
		span.setTag("valor", transferencia.getValor()); 
		
		try
		{
			//Scope scope = tracer.scopeManager().activate(span, true);
			logger.info("Inicio da transferencia: " + transferencia.getAgenciaOrigem() + ", " + transferencia.getContaOrigem() + " valor " + transferencia.getValor()+ " para a agencia "+ transferencia.getAgenciaDestino()+ ", " + transferencia.getContaDestino());
			RestTemplate client = new RestTemplate();
			//ResponseEntity<br.com.itau.modernizacao.tef.controller.dto.ContaCorrente> resposta;
			
			try
			{
				logger.debug("Vai tentar recuperar os dados da conta de origem");
				String url = "http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaOrigem() +"_" + transferencia.getContaOrigem();
		        URI uri = UriComponentsBuilder.fromHttpUrl(url).build(Collections.emptyMap());
		        this.get(span,"get-Conta-Origem", uri, br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class, client);
				//client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaOrigem() +"_" + transferencia.getContaOrigem(),br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class);

			}
			catch (Exception e)
			{ 
				logger.error("Conta Origem nao existe! agencia: " + transferencia.getAgenciaOrigem() + " conta: " + transferencia.getContaOrigem());
				span.log("Conta Origem não existe");
				span.setTag("sucesso", false);

				return ResponseEntity.notFound().build();
			}
			try
			{
				logger.debug("Vai recuperar dados da conta de destino");
				String url = "http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaDestino() +"_" + transferencia.getContaDestino();
		        URI uri = UriComponentsBuilder.fromHttpUrl(url).build(Collections.emptyMap());
		        this.get(span, "get-Conta-Destino", uri, br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class, client);
				//client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaDestino() +"_" + transferencia.getContaDestino(),br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class);

			}
			catch (Exception e)
			{
				logger.error("Conta Destino nao existe! agencia: " + transferencia.getAgenciaDestino() + " conta: " + transferencia.getContaDestino());
				span.log("Conta Destino não existe");
				span.setTag("sucesso", false);
				return ResponseEntity.notFound().build();
			}
			
			String transfer = this.formatMessage(transferencia, span);
			
			logger.debug("Vai enviar mensagem para o Kafka para iniciar a transação: " + transfer);
			kafka.sendMessage(transfer.toString(), span, "simulacao", tracer, kafkaTemplate, "tef_envioMensagem");
			
			logger.debug("Aguarda resposta da validação da senha");
			while (this.aguardaSenha)
			{
				java.util.Date agora = new java.util.Date();
				if (agora.getTime() - hoje.getTime() > 20000 && this.aguardaSenha)
				{
					
					logger.error("Timeout aguardando validação da senha! " + this.aguardaSenha);
					span.log("Timeout aguardando resposta da validação da Senha");
					span.setTag("sucesso", false);

					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
				}
				if (!this.aguardaSenha)
				{
					break;
				}
			}
			logger.debug("Aguarda resposta da validação do Saldo");
			while (this.aguardaSaldo)
			{
				java.util.Date agora = new java.util.Date();
				if (agora.getTime() - hoje.getTime() > 20000 && this.aguardaSaldo)
				{
					logger.error("Timeout aguardando resultado do Saldo! " + this.aguardaSaldo);
					span.log("Timeout aguardando resposta da validação do saldo");
					span.setTag("sucesso", false);

					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
				}

				if (!this.aguardaSaldo)
				{
					break;
				}
			}
			
			logger.debug("Prepara o resultado");
			ResultadoTransferenciaDTO resultado = this.preparaResultado(transferencia, span);
			
			if (!this.respostaSenha)
			{
				logger.error("Transferencia nao executada por problema com a senha");
				resultado.setSucesso(false);
				resultado.setMensagem("Senha invalida");
				span.log("Senha invalida");
				span.setTag("sucesso", false);
				
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			else if (!this.respostaSaldo)
			{
				logger.error("Transferencia nao executada por saldo insuficiente");
				resultado.setSucesso(false);
				resultado.setMensagem("Saldo Insuficiente");
				span.log("Saldo Insuficiente");
				span.setTag("sucesso", false);

				return ResponseEntity.ok(resultado);
				
			}
			logger.info("Transferencia efetuada com sucesso! agencia: " + transferencia.getAgenciaOrigem() + ", conta: " + transferencia.getContaOrigem() + ", valor:  R$ " + transferencia.getValor() + " para a agencia: " + transferencia.getAgenciaDestino() + ", conta: " + transferencia.getContaDestino());
			
			span.setTag("sucesso", true);

			resultado.setSucesso(true);
			resultado.setMensagem("transferencia efetuada com sucesso!");
			
			return ResponseEntity.ok(resultado);
		}
		catch (Exception e)
		{
			logger.error("Falha no processo de transferencia, mensagem: " + e.getMessage());
			span.log("Falha no processo de transferencia: " + e.getMessage());
			span.setTag("sucesso", false);
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}
		finally
		{
			this.aguardaSenha=true;
			this.aguardaSaldo=true;
			
			span.finish();
		
		}
		 
	}
	
	
	@KafkaListener(topics="senha", groupId = "tef-groupid")
	public void senha(String mensagem, @Headers MessageHeaders headers)
	{
		Span span = kafka.startConsumerSpan("tef_validaSenha_kafka", headers, tracer);
		try
		{
			//Scope scope = tracer.scopeManager().activate(span, false);
			logger.debug("Resposta senha " + mensagem);
			this.respostaSenha = new Boolean(mensagem);
		
			this.aguardaSenha=false;
		}
		finally
		{
			span.finish();
		}
	}
	@KafkaListener(topics="saldo", groupId = "tef-groupid")
	public void saldo(String mensagem, @Headers MessageHeaders headers)
	{
		Span span = kafka.startConsumerSpan("tef_validaSaldo_kafka", headers, tracer);
		//Span span = tracer.buildSpan("tef_validaSaldo_kafka").start();
		try
		{
			//Scope scope = tracer.scopeManager().activate(span,false);
			logger.debug("Resposta saldo " + mensagem);
			this.respostaSaldo = new Boolean(mensagem);
			this.aguardaSaldo=false;
		}
		finally
		{
			span.finish();
		}
	}
}
