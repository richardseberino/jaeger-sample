package br.com.itau.modernizacao.senha;

import java.util.Optional;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.modernizacao.senha.modelo.Senha;
import br.com.itau.modernizacao.senha.modelo.dto.SenhaValidacaoDto;
import br.com.itau.modernizacao.senha.repository.SenhaRepository;
import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/senha")
public class SenhaController {

	private static final Logger logger = LoggerFactory.getLogger(SenhaController.class);
	
	@Autowired
	private SenhaRepository senhaRepository;
	
	@PostMapping
	@Transactional
	@Timed("a_service")
	public ResponseEntity<Boolean> validaSenha(@RequestBody SenhaValidacaoDto valida)
	{
		logger.info("Valiando a senha: " + valida);
		Optional<Senha> senha = senhaRepository.findById(valida.getContaID());
		if (senha.isPresent() )
		{
			if (senha.get().getSenha().equals(valida.getSenha()))
			{
				logger.info("Senha válida");
				return ResponseEntity.ok(Boolean.TRUE);
			}
			else
			{
				logger.info("Senha invalida");
				return ResponseEntity.ok(Boolean.FALSE);
			}
		}
		logger.error("Conta não encontrada pelo ID: " + valida.getContaID());
		return ResponseEntity.notFound().build();
	}
}
