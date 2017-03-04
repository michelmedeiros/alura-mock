package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.infra.utils.Relogio;

import java.util.Calendar;

/**
 * Created by Michel Medeiros on 04/03/2017.
 */
public class RelogioDoSistema implements Relogio {
    @Override
    public Calendar hoje() {
        return Calendar.getInstance();
    }
}
