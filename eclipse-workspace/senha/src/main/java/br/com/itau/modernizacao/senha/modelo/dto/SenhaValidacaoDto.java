package br.com.itau.modernizacao.senha.modelo.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class SenhaValidacaoDto {

	@NotNull @NotEmpty
	private String senha;
	
	@NotNull @NotEmpty
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
