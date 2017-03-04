package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.email.EnviadorDeEmail;
import lombok.extern.slf4j.Slf4j;

import java.util.Calendar;
import java.util.List;

@Slf4j
public class EncerradorDeLeilao {

	private final RepositorioDeLeiloes dao;
	private final EnviadorDeEmail carteiro;

	private int total = 0;

	public EncerradorDeLeilao(RepositorioDeLeiloes leilaoDao, EnviadorDeEmail carteiro) {
		this.dao = leilaoDao;
		this.carteiro = carteiro;
	}


	public void encerra() throws Exception {
		List<Leilao> todosLeiloesCorrentes = dao.correntes();

		for (Leilao leilao : todosLeiloesCorrentes) {
			try {
				if (comecouSemanaPassada(leilao)) {
					leilao.encerra();
					total++;
					dao.atualiza(leilao);
					carteiro.envia(leilao);
				}
			} catch (Exception ex) {
				log.error("Erro ao encerrar leilão: {}" + ex.getMessage());
				throw new Exception("Erro ao encerrar leilão");
			}

		}
	}

	private boolean comecouSemanaPassada(Leilao leilao) {
		return diasEntre(leilao.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalEncerrados() {
		return total;
	}
}
