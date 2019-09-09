package br.com.itau.modernizacao.senha.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import br.com.itau.modernizacao.senha.modelo.Senha;

public interface SenhaRepository extends JpaRepository<Senha, Long> {

	@Query("select t from Senha t where t.id = :idConta and t.senha = :senha")
	Optional<Senha> pesquisaSenha(Long idConta, String senha);
}
