package br.com.caelum.leilao.infra.dao;

import br.com.caelum.leilao.dominio.Pagamento;

/**
 * Created by Michel Medeiros on 03/03/2017.
 */
public interface RepositorioDePagamentos {
    public void salva(Pagamento pagamento);
}
