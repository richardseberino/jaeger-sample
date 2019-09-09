package br.com.itau.modernizacao.cc.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.com.itau.modernizacao.cc.modelo.ContaCorrente;

public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long>{

	@Query("SELECT t from ContaCorrente t where t.agencia = :agencia and t.conta = :conta")
	List<ContaCorrente> pesquisaPorAgenciaConta(@Param("agencia") int agencia, @Param("conta") int conta);

}
