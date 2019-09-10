package br.com.itau.modernizacao.kafka.saldo;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.micrometer.core.annotation.Timed;


@Service
public class SaldoService {

	@Autowired
	private final Producer producer;
	
	public SaldoService(Producer producer)
	{
		this.producer = producer;
	}
	
	@KafkaListener(topics="simulacao", groupId = "saldo-groupid" )
	public void validaSaldo(String mensagem)
	{
		try
		{
		
			String[] msg = mensagem.split(";");
			int agenciaOrigem = Integer.parseInt(msg[0]);
			int contaOrigem = Integer.parseInt(msg[1]);
			double valor = Double.parseDouble(msg[4]);
			
			RestTemplate client = new RestTemplate();
			ResponseEntity<ContaCorrente> resposta = client.getForEntity("http://contacorrente-svc:9002/contas/agencia/" + agenciaOrigem +"_" + contaOrigem,ContaCorrente.class);

			if (resposta.getBody().getSaldo()<valor)
			{
				producer.sendMessage("false");
			}
			else
			{
				producer.sendMessage("true");
			}
		}
		catch (Exception e)
		{
			System.out.println("Falha ao processar Saldo: " + e.getMessage());
			//e.printStackTrace();
			producer.sendMessage("false");
		}
	}
	

	
	
}
