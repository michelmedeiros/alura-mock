package br.com.caelum.leilao.servico;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.email.EnviadorDeEmail;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

/**
 * Created by Michel Medeiros on 02/03/2017.
 */
@RunWith(SpringRunner.class)
public class EncerradorDeLeilaoTest {

    @Mock
    private RepositorioDeLeiloes leilaoDao;

    @Mock
    private EnviadorDeEmail carteiro;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() throws Exception {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(antiga).constroi();

        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();
        assertThat(encerrador.getTotalEncerrados(), is(2));
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }

    @Test
    public void naoDeveEncerrarLeiloesQueComecaramOntem() throws Exception {
        Calendar ontem = Calendar.getInstance();
        ontem.add(Calendar.DAY_OF_WEEK, -1);


        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(ontem).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(ontem).constroi();

        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();
        assertThat(encerrador.getTotalEncerrados(), is(0));
        assertFalse(leilao1.isEncerrado());
        assertFalse(leilao2.isEncerrado());
        verify(leilaoDao, never()).atualiza(leilao1);
        verify(leilaoDao, never()).atualiza(leilao2);
    }

    @Test
    public void naoExistemLeiloesParaEncerramento() throws Exception {
        when(leilaoDao.correntes()).thenReturn(new ArrayList<>());
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();
        assertThat(encerrador.getTotalEncerrados(), is(0));
    }

    @Test
    public void deveAtualizarLeiloesEncerrados() throws Exception {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);
        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1));
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();
        assertThat(encerrador.getTotalEncerrados(), is(1));
        assertTrue(leilao1.isEncerrado());
        verify(leilaoDao, times(1)).atualiza(leilao1);

        // passamos os mocks que serao verificados
        InOrder inOrder = inOrder(leilaoDao, carteiro);
        // a primeira invocação
        inOrder.verify(leilaoDao, times(1)).atualiza(leilao1);
        // a segunda invocação
        inOrder.verify(carteiro, times(1)).envia(leilao1);
    }

    @Test
    public void deveLancarExcecaoA0EncerramentoLeilaoQuandoDaoFalha() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Erro ao encerrar leilão");
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);
        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Mac Book Pro")
                .naData(antiga).constroi();

        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(leilaoDao).atualiza(leilao1);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();

        // passamos os mocks que serao verificados
        // a primeira invocação
        verify(leilaoDao).atualiza(leilao1);
        // a segunda invocação não será realizada
        verify(carteiro, never()).envia(leilao1);
    }

    @Test
    public void deveLancarExcecaoA0EncerramentoLeilaoQuandoEmailFalha() throws Exception {
        expectedException.expect(Exception.class);
        expectedException.expectMessage("Erro ao encerrar leilão");
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);
        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Mac Book Pro")
                .naData(antiga).constroi();

        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        doThrow(new RuntimeException()).when(carteiro).envia(leilao1);
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao, carteiro);
        encerrador.encerra();

        // passamos os mocks que serao verificados
        // a primeira invocação
        verify(leilaoDao).atualiza(leilao1);
        // a segunda invocação não será realizada
        verify(carteiro).envia(leilao1);

    }
}