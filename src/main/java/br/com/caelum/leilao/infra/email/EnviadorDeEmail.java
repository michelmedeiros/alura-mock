package br.com.caelum.leilao.infra.email;

import br.com.caelum.leilao.dominio.Leilao;

/**
 * Created by Michel Medeiros on 02/03/2017.
 */
public interface EnviadorDeEmail {

    void envia(Leilao leilao);
}
