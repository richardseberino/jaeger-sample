package br.com.itau.modernizacao.senha;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport
public class SenhaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SenhaApplication.class, args);
	}

}
