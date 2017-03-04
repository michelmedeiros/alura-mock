package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;
import br.com.caelum.leilao.infra.utils.Relogio;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Michel Medeiros on 03/03/2017.
 */
@RunWith(SpringRunner.class)
public class GeradorDePagamentosTest {

    @Mock
    private RepositorioDeLeiloes leiloes;

    @Mock
    private RepositorioDePagamentos pagamentos;

    @Mock
    private Relogio relogioDoSistema;

    private Avaliador avaliador;

    @Before
    public void setUp() {
        avaliador = new Avaliador();
    }

    @Test
    public void deveGerarPagamentoParaUmLeilaoEncerrado() {

        Leilao leilao = new CriadorDeLeilao().para("Playstation").lance(new Usuario("Jose"), 2000.0)
        .lance(new Usuario("Maria"), 2500.0).constroi();
        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        GeradorDePagamentos geradorDePagamentos = new GeradorDePagamentos(leiloes, avaliador, pagamentos, new RelogioDoSistema());
        geradorDePagamentos.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());
        Pagamento pagamentoGerado = argumento.getValue();
        assertThat(pagamentoGerado.getValor(), equalTo(2500.0));
    }

    @Test
    public void deveEmpurrarPagamentoDeSabadoParaProximoDiaUtil() {
        //31/12/2016 Sábado
        Calendar sabado = Calendar.getInstance();
        sabado.set(2016, Calendar.DECEMBER, 31);

        // ensinamos o mock a dizer que "hoje" é sabado!
        when(relogioDoSistema.hoje()).thenReturn(sabado);

        Leilao leilao = new CriadorDeLeilao()
                .para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
                .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        GeradorDePagamentos geradorDePagamentos = new GeradorDePagamentos(leiloes, avaliador, pagamentos, relogioDoSistema);
        geradorDePagamentos.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());
        Pagamento pagamentoGerado = argumento.getValue();
        Calendar dataEsperada = Calendar.getInstance();
        dataEsperada.set(2017, Calendar.JANUARY, 2);
        assertDataEsperada(pagamentoGerado, dataEsperada);
    }

    @Test
    public void deveEmpurrarPagamentoDeDomingoParaProximoDiaUtil() {
        //30/04/2017 Domingo
        Calendar domingo = Calendar.getInstance();
        domingo.set(2017, Calendar.APRIL, 30);

        // ensinamos o mock a dizer que "hoje" é domingo!
        when(relogioDoSistema.hoje()).thenReturn(domingo);

        Leilao leilao = new CriadorDeLeilao()
                .para("Playstation")
                .lance(new Usuario("José da Silva"), 2000.0)
                .lance(new Usuario("Maria Pereira"), 2500.0)
               .constroi();

        when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));

        GeradorDePagamentos geradorDePagamentos = new GeradorDePagamentos(leiloes, avaliador, pagamentos, relogioDoSistema);
        geradorDePagamentos.gera();

        ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
        verify(pagamentos).salva(argumento.capture());
        Pagamento pagamentoGerado = argumento.getValue();
        Calendar dataEsperada = Calendar.getInstance();
        dataEsperada.set(2017, Calendar.MAY, 1);
        assertDataEsperada(pagamentoGerado, dataEsperada);
    }

    private void assertDataEsperada(Pagamento pagamentoGerado, Calendar dataEsperada) {
        assertThat(pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK), equalTo(dataEsperada.get(Calendar.DAY_OF_WEEK)));
        assertThat(pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH), equalTo(dataEsperada.get(Calendar.DAY_OF_MONTH)));
    }
}