package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;
import br.com.caelum.leilao.infra.utils.Relogio;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Michel Medeiros on 03/03/2017.
 */
public class GeradorDePagamentos {

    private final RepositorioDeLeiloes repositorioDeLeiloes;
    private final Avaliador avaliador;
    private final RepositorioDePagamentos pagamentos;
    private final Relogio relogioDoSistema;

    public GeradorDePagamentos(RepositorioDeLeiloes repositorioDeLeiloes, Avaliador avaliador, RepositorioDePagamentos pagamentos, Relogio relogioDoSistema) {
        this.repositorioDeLeiloes = repositorioDeLeiloes;
        this.avaliador = avaliador;
        this.pagamentos = pagamentos;
        this.relogioDoSistema = relogioDoSistema;
    }

    public void gera() {
        List<Leilao> leiloesEncerrados = this.repositorioDeLeiloes.encerrados();
        leiloesEncerrados.forEach(leilao -> {
            this.avaliador.avalia(leilao);
            Pagamento pagamento = new Pagamento(avaliador.getMaiorLance(), getPrimeiroDiaUtil());
            this.pagamentos.salva(pagamento);
        });
    }

    public Calendar getPrimeiroDiaUtil() {
        Calendar primeiroDiaUtil = relogioDoSistema.hoje();
        int diaAtual = primeiroDiaUtil.get(Calendar.DAY_OF_WEEK);
        if(diaAtual == Calendar.SATURDAY) {
            primeiroDiaUtil.add(Calendar.DAY_OF_WEEK, 2);
        } else if(diaAtual == Calendar.SUNDAY) {
            primeiroDiaUtil.add(Calendar.DAY_OF_WEEK, 1);
        }
        return primeiroDiaUtil;
    }



}
