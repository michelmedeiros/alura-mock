package br.com.caelum.leilao.servico;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import br.com.caelum.leilao.infra.dao.LeilaoDao;
import org.junit.Before;
import org.junit.Test;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Lance;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Usuario;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AvaliadorTest {
	
	private Avaliador leiloeiro;
	private Usuario maria;
	private Usuario jose;
	private Usuario joao;

	@Mock
    private LeilaoDao leilaoDao;

    public Avaliador getLeiloeiro() {
        return leiloeiro;
    }

    @Before
	public void criaAvaliador() {
		this.leiloeiro = new Avaliador();
		this.joao = new Usuario("João");
		this.jose = new Usuario("José");
		this.maria = new Usuario("Maria");
	}
	
	@Test(expected=RuntimeException.class)
	public void naoDeveAvaliarLeiloesSemNenhumLanceDado() {
		Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo").constroi();
		
		leiloeiro.avalia(leilao);
		
	}
	
    @Test
    public void deveEntenderLancesEmOrdemCrescente() {
        // parte 1: cenario
         
        Leilao leilao = new Leilao("Playstation 3 Novo");
         
        leilao.propoe(new Lance(joao, 250.0));
        leilao.propoe(new Lance(jose, 300.0));
        leilao.propoe(new Lance(maria, 400.0));
         
        // parte 2: acao
        leiloeiro.avalia(leilao);
         
        // parte 3: validacao
        assertThat(leiloeiro.getMaiorLance(), equalTo(400.0));
        assertThat(leiloeiro.getMenorLance(), equalTo(250.0));
    }
 
    @Test
    public void deveEntenderLeilaoComApenasUmLance() {
    	Usuario joao = new Usuario("João");
        Leilao leilao = new Leilao("Playstation 3 Novo");
         
        leilao.propoe(new Lance(joao, 1000.0));
         
        leiloeiro.avalia(leilao);
         
        assertEquals(1000.0, leiloeiro.getMaiorLance(), 0.00001);
        assertEquals(1000.0, leiloeiro.getMenorLance(), 0.00001);
    }
     
    @Test
    public void deveEncontrarOsTresMaioresLances() {
        
        Leilao leilao = new CriadorDeLeilao().para("Playstation 3 Novo")
        		.lance(joao, 100.0)
        		.lance(maria, 200.0)
        		.lance(joao, 300.0)
        		.lance(maria, 400.0)
        		.constroi();
         
        leiloeiro.avalia(leilao);
         
        List<Lance> maiores = leiloeiro.getTresMaiores();
        assertEquals(3, maiores.size());
        
        assertThat(maiores, hasItems(
        		new Lance(maria, 400),
        		new Lance(joao, 300),
        		new Lance(maria, 200)
        ));
        
    }

    @Test
    public void deveEncerrarLeiloesQueComecaramUmaSemanaAtras() {
        Calendar antiga = Calendar.getInstance();
        antiga.set(1999, 1, 20);

        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
                .naData(antiga).constroi();
        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
                .naData(antiga).constroi();

        when(leilaoDao.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));
        EncerradorDeLeilao encerrador = new EncerradorDeLeilao(leilaoDao);
        encerrador.encerra();
        assertThat(encerrador.getTotalEncerrados(), is(2));
        assertTrue(leilao1.isEncerrado());
        assertTrue(leilao2.isEncerrado());
    }


     
}
