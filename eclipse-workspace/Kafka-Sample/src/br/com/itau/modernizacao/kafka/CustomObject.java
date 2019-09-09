package br.com.itau.modernizacao.kafka;

public class CustomObject {

	private int agenciaOrigem;
	private int contaOrigem;
	private double valor;
	private int agenciaDestino;
	private int contaDestino;
	
	
	public int getAgenciaOrigem() {
		return agenciaOrigem;
	}
	public void setAgenciaOrigem(int agenciaOrigem) {
		this.agenciaOrigem = agenciaOrigem;
	}
	public int getContaOrigem() {
		return contaOrigem;
	}
	public void setContaOrigem(int contaOrigem) {
		this.contaOrigem = contaOrigem;
	}
	public double getValor() {
		return valor;
	}
	public void setValor(double valor) {
		this.valor = valor;
	}
	public int getAgenciaDestino() {
		return agenciaDestino;
	}
	public void setAgenciaDestino(int agenciaDestino) {
		this.agenciaDestino = agenciaDestino;
	}
	public int getContaDestino() {
		return contaDestino;
	}
	public void setContaDestino(int contaDestino) {
		this.contaDestino = contaDestino;
	}
	
	
}
