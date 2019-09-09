package br.com.itau.modernizacao.tef.controller.dto;

public class TransferenciaDTO {
	
	private int agenciaOrigem;
	private int agenciaDestino;
	private int contaOrigem;
	private int contaDestino;
	private double valor;
	private String senha;
	
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public int getAgenciaOrigem() {
		return agenciaOrigem;
	}
	public void setAgenciaOrigem(int agenciaOrigem) {
		this.agenciaOrigem = agenciaOrigem;
	}
	public int getAgenciaDestino() {
		return agenciaDestino;
	}
	public void setAgenciaDestino(int agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}
	public int getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(int contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	public int getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(int contaDestino) {
		this.contaDestino = contaDestino;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	
}
