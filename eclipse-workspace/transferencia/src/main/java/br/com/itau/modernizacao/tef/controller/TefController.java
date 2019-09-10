package br.com.itau.modernizacao.tef.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import br.com.itau.modernizacao.tef.Producer;
import br.com.itau.modernizacao.tef.controller.dto.ResultadoTransferenciaDTO;
import br.com.itau.modernizacao.tef.controller.dto.TransferenciaDTO;
import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/tef")
public class TefController {

	private final Producer producer;
	
	private boolean aguardaSenha = true;
	private boolean respostaSenha = false;
	private boolean aguardaSaldo = true;
	private boolean respostaSaldo = false;
	
	@Autowired
	public TefController(Producer producer)
	{
		this.producer = producer;
	}
	
	@PostMapping("/simula")
	@Timed("a_service")
	public ResponseEntity<ResultadoTransferenciaDTO> simulaTranaferencia(@RequestBody TransferenciaDTO transferencia)
	{
		java.util.Date hoje = new java.util.Date();
		
		try
		{
			//System.out.println("Inicio da transferencia: " + transferencia.getAgenciaOrigem() + ", " + transferencia.getContaOrigem() + " valor " + transferencia.getValor()+ " para a agencia "+ transferencia.getAgenciaDestino()+ ", " + transferencia.getContaDestino());
			RestTemplate client = new RestTemplate();
			ResponseEntity<br.com.itau.modernizacao.tef.controller.dto.ContaCorrente> resposta;
			try
			{
				resposta = client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaOrigem() +"_" + transferencia.getContaOrigem(),br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class);

			}
			catch (Exception e)
			{ 
				System.err.println("Conta Origem nao existe! agencia: " + transferencia.getAgenciaOrigem() + " conta: " + transferencia.getContaOrigem());
				return ResponseEntity.notFound().build();
			}
			try
			{
				resposta = client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + transferencia.getAgenciaDestino() +"_" + transferencia.getContaDestino(),br.com.itau.modernizacao.tef.controller.dto.ContaCorrente.class);

			}
			catch (Exception e)
			{
				System.err.println("Conta Destino nao existe! agencia: " + transferencia.getAgenciaDestino() + " conta: " + transferencia.getContaDestino());
				return ResponseEntity.notFound().build();
			}
			
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
			
			
			this.producer.sendMessage(transfer.toString());
			
			while (this.aguardaSenha)
			{
				java.util.Date agora = new java.util.Date();
				if (agora.getTime() - hoje.getTime() > 20000 && this.aguardaSenha)
				{
					
					System.err.println("Timeout aguardando validação da senha! " + this.aguardaSenha);
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
				}
				if (!this.aguardaSenha)
				{
					break;
				}
			}
			while (this.aguardaSaldo)
			{
				java.util.Date agora = new java.util.Date();
				if (agora.getTime() - hoje.getTime() > 20000 && this.aguardaSaldo)
				{
					System.err.println("Timeout aguardando resultado do Saldo! " + this.aguardaSaldo);
					return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT).build();
				}

				if (!this.aguardaSaldo)
				{
					break;
				}
			}
			
			ResultadoTransferenciaDTO resultado = new ResultadoTransferenciaDTO();
			resultado.setAgenciaDestino(transferencia.getAgenciaDestino());
			resultado.setAgenciaOrigem(transferencia.getAgenciaOrigem());
			resultado.setContaDestino(transferencia.getContaDestino());
			resultado.setContaOrigem(transferencia.getContaOrigem());
			resultado.setValor(transferencia.getValor());
			if (!this.respostaSenha)
			{
				resultado.setSucesso(false);
				resultado.setMensagem("Senha invalida");
				return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
			}
			else if (!this.respostaSaldo)
			{
				resultado.setSucesso(false);
				resultado.setMensagem("Saldo Insuficiente");
				return ResponseEntity.ok(resultado);
				
			}
			resultado.setSucesso(true);
			resultado.setMensagem("transferencia efetuada com sucesso!");
			
			return ResponseEntity.ok(resultado);
		}
		catch (Exception e)
		{
			System.err.println("Erro: " + e.getMessage());
			return ResponseEntity.badRequest().build();
		}
		finally
		{
			this.aguardaSenha=true;
			this.aguardaSaldo=true;
		}
	}
	
	
	@KafkaListener(topics="senha", groupId = "tef-groupid")
	public void senha(String mensagem)
	{
		System.out.println("Resposta senha " + mensagem);
		this.respostaSenha = new Boolean(mensagem);
		this.aguardaSenha=false;
	}
	@KafkaListener(topics="saldo", groupId = "tef-groupid")
	public void saldo(String mensagem)
	{
		System.out.println("Resposta saldo " + mensagem);
		this.respostaSaldo = new Boolean(mensagem);
		this.aguardaSaldo=false;
	}
}
