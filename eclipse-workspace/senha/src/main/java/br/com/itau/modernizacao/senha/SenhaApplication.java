package br.com.itau.modernizacao.senha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import io.jaegertracing.Configuration;
import io.jaegertracing.Configuration.ReporterConfiguration;
import io.jaegertracing.Configuration.SamplerConfiguration;
import io.jaegertracing.internal.samplers.ProbabilisticSampler;

@SpringBootApplication
@EnableSpringDataWebSupport
public class SenhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SenhaApplication.class, args);
	}

	@Bean
	public io.opentracing.Tracer tracer() {
		
		//SamplerConfiguration samplerConfig = new SamplerConfiguration().withType(ConstSampler.TYPE).withParam(1);
		SamplerConfiguration samplerConfig = new SamplerConfiguration().withType(ProbabilisticSampler.TYPE).withParam(1);
        ReporterConfiguration reporterConfig = new ReporterConfiguration().withLogSpans(true);
        return new Configuration("SenhaService").withSampler(samplerConfig).withReporter(reporterConfig).getTracer();

	}	
	
}
