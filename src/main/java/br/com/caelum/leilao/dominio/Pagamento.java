package br.com.caelum.leilao.dominio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Calendar;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Pagamento {
	private double valor;
	private Calendar data;

}
