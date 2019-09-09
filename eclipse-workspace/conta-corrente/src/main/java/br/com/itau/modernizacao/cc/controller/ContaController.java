package br.com.itau.modernizacao.cc.controller;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.modernizacao.cc.modelo.ContaCorrente;
import br.com.itau.modernizacao.cc.repository.ContaCorrenteRepository;
import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/contas")
public class ContaController {

	@Autowired
	private ContaCorrenteRepository contaRepository;
	
	@GetMapping
	public List<ContaCorrente> listar(@RequestParam(required=false) String agencia, @RequestParam(required=false) String conta)
	{
		if (agencia==null || agencia.isEmpty() || conta==null || conta.isEmpty())
		{
			return contaRepository.findAll();
		}
		
		return contaRepository.pesquisaPorAgenciaConta(Integer.parseInt(agencia), Integer.parseInt(conta));
	}
	
	@GetMapping("/agencia/{idconta}")
	@Timed("a_service")
	public ResponseEntity<ContaCorrente> detalheConta(@PathVariable String idconta)
	{
		//try { Thread.sleep(300); } catch (Exception e) { }
		try
		{
			String[] split = idconta.split("_");
			int agencia = Integer.parseInt(split[0]);
			int conta = Integer.parseInt(split[1]);
			List<ContaCorrente> contas = contaRepository.pesquisaPorAgenciaConta(agencia, conta);
			if (contas.size()>0)
			{
				return ResponseEntity.ok(contas.get(0));
			}
			else
			{
				return ResponseEntity.notFound().build();
			}
		}
		catch (Exception e)
		{
			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("{id}")
	public ResponseEntity<ContaCorrente> detalheConta(@PathVariable Long id)
	{
		Optional<ContaCorrente> conta =contaRepository.findById(id);
		if (conta.isPresent())
		{
			return ResponseEntity.ok(conta.get());
		}
		return ResponseEntity.notFound().build();
	}
	
	
	
	
}
