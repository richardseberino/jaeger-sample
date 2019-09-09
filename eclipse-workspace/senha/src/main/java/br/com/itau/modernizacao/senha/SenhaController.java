package br.com.itau.modernizacao.senha;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

	@Autowired
	private SenhaRepository senhaRepository;
	
	@PostMapping
	@Transactional
	@Timed("a_service")
	public ResponseEntity<Boolean> validaSenha(@RequestBody SenhaValidacaoDto valida)
	{
		Optional<Senha> senha = senhaRepository.findById(valida.getContaID());
		if (senha.isPresent() )
		{
			//try { Thread.sleep(300); } catch (Exception e) { }
			if (senha.get().getSenha().equals(valida.getSenha()))
			{
				return ResponseEntity.ok(Boolean.TRUE);
			}
			else
			{
				return ResponseEntity.ok(Boolean.FALSE);
			}
		}
		return ResponseEntity.notFound().build();
	}
}
