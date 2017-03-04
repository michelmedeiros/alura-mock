package br.com.caelum.leilao.dominio;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode
public class Lance {

	private Usuario usuario;
	private double valor;
	private int id;
	
	public Lance(Usuario usuario, double valor) {
		this.usuario = usuario;
		this.valor = valor;
	}
}
