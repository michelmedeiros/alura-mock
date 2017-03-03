package br.com.caelum.leilao.infra.dao;

import br.com.caelum.leilao.dominio.Leilao;

import java.util.List;

/**
 * Created by Michel Medeiros on 02/03/2017.
 */
public interface RepositorioDeLeiloes {
    void salva(Leilao leilao);
    List<Leilao> encerrados();
    List<Leilao> correntes();
    void atualiza(Leilao leilao);
}
