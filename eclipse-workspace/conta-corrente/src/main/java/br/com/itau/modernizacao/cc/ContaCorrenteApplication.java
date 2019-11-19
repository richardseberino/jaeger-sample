package br.com.itau.modernizacao.cc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;


@SpringBootApplication
public class ContaCorrenteApplication {

	public static void main(String[] args) {
		SpringApplication.run(ContaCorrenteApplication.class, args);
	}

	@Bean
	public io.opentracing.Tracer tracer() {
		
		//SamplerConfiguration samplerConfig = new SamplerConfiguration().withType(ConstSampler.TYPE).withParam(1);
		SamplerConfiguration samplerConfig = new SamplerConfiguration().withType(ProbabilisticSampler.TYPE).withParam(1);
        ReporterConfiguration reporterConfig = new ReporterConfiguration().withLogSpans(true);
        return new Configuration("ContaCorrente").withSampler(samplerConfig).withReporter(reporterConfig).getTracer();

	}
	
}
