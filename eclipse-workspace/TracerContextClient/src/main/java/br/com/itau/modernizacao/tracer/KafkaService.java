package br.com.itau.modernizacao.tracer;


import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;


import io.opentracing.References;

import io.opentracing.Span;
import io.opentracing.SpanContext;
import io.opentracing.Tracer;
import io.opentracing.propagation.Format;
import io.opentracing.propagation.TextMap;

@Service
public class KafkaService {


    
	private static final Logger logger = LoggerFactory.getLogger(KafkaService.class);

	

    public Span startConsumerSpan(String name, MessageHeaders headers, Tracer tracer) {
    	//System.out.println("header do Kafka: " + headers.toString());
        logger.info("Abrindfo o header do Kafka, recebendo mensagem: " + headers);
    	TextMap carrier = new KafkaHeaderMap(headers);
        SpanContext parent = tracer.extract(Format.Builtin.TEXT_MAP, carrier);
        return tracer.buildSpan(name) //
                .addReference(References.FOLLOWS_FROM, parent) //
                .start();
    }

	public void sendMessage(String message, Span spanPai, String topico, Tracer tracer, KafkaTemplate<String, Message> kafkaTemplate, String spanId)
	{
		KafkaHeaderMap h1 = new KafkaHeaderMap();
		Span span = tracer.buildSpan(spanId).asChildOf(spanPai).start();
		tracer.inject(span.context(), Format.Builtin.TEXT_MAP, h1);
		logger.info("abrindo o header h1: " + h1.toString());
		//Scope scope = tracer.scopeManager().activate(span, false);
		logger.info(String.format("Enviando mensagem para topico " + topico + " --> %s",message));
		span.setTag("kafka.message", message);
		span.setTag("kafka.topico", topico); 
		span.setTag("span.kind", "KafkaProducer");
		Entry<String, String> item = h1.getContext();
		Message<String> mensagem = MessageBuilder
				.withPayload(message)
				.setHeader(KafkaHeaders.TOPIC, topico)
				.setHeader("second_span_" + item.getKey(), item.getValue())
				.build();
		kafkaTemplate.send(mensagem);
		span.finish();
	}

    
}
