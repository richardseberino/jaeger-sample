package br.com.alura.forum.repository;

import org.springframework.data.repository.Repository;

import br.com.alura.forum.modelo.Curso;

public interface CursoRepository extends Repository<Curso, Long> {

	Curso findByNome(String nome);
}
