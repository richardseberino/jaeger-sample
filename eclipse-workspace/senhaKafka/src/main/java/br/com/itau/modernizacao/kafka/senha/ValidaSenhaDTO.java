package br.com.itau.modernizacao.kafka.senha;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class ValidaSenhaDTO {

	private String senha;

	private Long contaID;

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public Long getContaID() {
		return contaID;
	}

	public void setContaID(Long contaID) {
		this.contaID = contaID;
	}
	
	
	
}
