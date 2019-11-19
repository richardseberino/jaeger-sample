package br.com.itau.modernizacao.cc.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.itau.modernizacao.cc.modelo.ContaCorrente;
import br.com.itau.modernizacao.cc.repository.ContaCorrenteRepository;
import br.com.itau.modernizacao.tracer.TracerContextClientApplication;
//import br.com.itau.modernizacao.tracer.TracerContextClientApplication;
import io.micrometer.core.annotation.Timed;
import io.opentracing.Span;

//import io.opentracing.Span;

@RestController
@RequestMapping("/contas")
public class ContaController extends TracerContextClientApplication {
	private static final Logger logger = LoggerFactory.getLogger(ContaController.class);
	
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
	public ResponseEntity<ContaCorrente> detalheConta(@PathVariable String idconta, HttpServletRequest request)
	{
		Span span = this.startServerSpan("ContaCorrenteService", request); // tracer.buildSpan("contaCorrenteService").start();
		
		try
		{
	//		Scope scope = tracer.scopeManager().activate(span, false);
			logger.info("Recupera detalhe da conta " + idconta);
			String[] split = idconta.split("_");
			int agencia = Integer.parseInt(split[0]);
			int conta = Integer.parseInt(split[1]);
			logger.debug("Agencia " + agencia + ", conta " + conta);
			span.setTag("agencia", agencia);
			span.setTag("conta", conta);
			List<ContaCorrente> contas = contaRepository.pesquisaPorAgenciaConta(agencia, conta);
			if (contas.size()>0)
			{
				logger.info("Conta encontrada: " + contas.get(0).toString());
				return ResponseEntity.ok(contas.get(0));
			}
			else
			{
				logger.info("Conta não encontrada com o id informado: " + idconta);
				return ResponseEntity.notFound().build();
			}
		}
		catch (Exception e)
		{
			logger.error("Falha ao recuperar dados da conta corrente id: " +idconta +", erro: " + e.getMessage());
			return ResponseEntity.notFound().build();
		}
		finally
		{
			span.finish();
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
