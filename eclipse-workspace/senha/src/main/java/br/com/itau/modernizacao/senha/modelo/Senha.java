package br.com.itau.modernizacao.senha.modelo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Senha {

	@Id
	private Long id;
	private String senha;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	
	
}
