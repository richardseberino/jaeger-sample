package br.com.itau.modernizacao.tef.controller.dto;

public class ResultadoTransferenciaDTO {
	private int agenciaOrigem;
	private int agenciaDestino;
	private int contaOrigem;
	private int contaDestino;
	private double valor;
	private String mensagem;
	private boolean sucesso;
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
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	public boolean isSucesso() {
		return sucesso;
	}
	public void setSucesso(boolean sucesso) {
		this.sucesso = sucesso;
	}
	
	
}
