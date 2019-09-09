package br.com.itau.modernizacao.kafka.senha;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.annotation.Timed;

@Service
public class SenhaService {

	@Autowired
	private final Producer producer;
	
	public SenhaService(Producer producer)
	{
		this.producer = producer;
	}
	
	@KafkaListener(topics="simulacao", groupId = "senha-groupid")
	public void validaSenha(String mensagem)
	{
		//props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafka-svc:9092");
		try
		{
			String[] msg = mensagem.split(";");
			int agenciaOrigem = Integer.parseInt(msg[0]);
			int contaOrigem = Integer.parseInt(msg[1]);
			String senha = msg[5];
			System.out.println("Validando a senha para a agencia " + agenciaOrigem + ", " + contaOrigem);
			RestTemplate client = new RestTemplate();
			ResponseEntity<ContaCorrente> resposta = client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + agenciaOrigem +"_" + contaOrigem,ContaCorrente.class);

			ValidaSenhaDTO dados = new ValidaSenhaDTO();
			dados.setContaID(resposta.getBody().getIdConta());
			dados.setSenha(senha);
			ResponseEntity<Boolean> validada = client.postForEntity("http://senha-svc:9004/senha", dados, Boolean.class);
			producer.sendMessage(validada.getBody().toString());
		}
		catch (Exception e)
		{
			System.out.println("Falha ao processar Senha: " + e.getMessage());
			e.printStackTrace();
			producer.sendMessage("false");
		}
	}
	
	
}
