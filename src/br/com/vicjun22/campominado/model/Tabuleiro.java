package br.com.vicjun22.campominado.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Tabuleiro implements CampoObservador {

    private final int LINHAS;
    private final int COLUNAS;
    private final int MINAS;

    private final List<Campo> CAMPOS = new ArrayList<>();
    private final List<Consumer<Boolean>> observadores = new ArrayList<>();

    public Tabuleiro(int linhas, int colunas, int minas) {
        this.LINHAS = linhas;
        this.COLUNAS = colunas;
        this.MINAS = minas;

        gerarCampos();
        associarVizinhos();
        sortearMinas();
    }

    public int getLinhas() {
        return LINHAS;
    }

    public int getColunas() {
        return COLUNAS;
    }

    public void paraCadaCampo(Consumer<Campo> funcao) {
        CAMPOS.forEach(funcao);
    }

    public void registrarObservador(Consumer<Boolean> observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(boolean resultado) {
        observadores.forEach(observador -> observador.accept(resultado));
    }

    private void gerarCampos() {
        for (int linha = 0; linha < LINHAS; linha++) {
            for (int coluna = 0; coluna < COLUNAS; coluna++) {
                Campo campo = new Campo(linha, coluna);
                campo.registrarObservador(this);
                CAMPOS.add(campo);
            }
        }
    }

    private void associarVizinhos() {
        for (Campo c1: CAMPOS) {
            for (Campo c2: CAMPOS) {
                c1.adicionarVizinho(c2);
            }
        }
    }

    private void sortearMinas() {
        long minasArmadas = 0L;
        Predicate<Campo> minado = Campo::isMinado;

        do {
            int aleatorio = (int) (Math.random() * CAMPOS.size());
            CAMPOS.get(aleatorio).minar();
            minasArmadas = CAMPOS.stream().filter(minado).count();
        } while (minasArmadas < MINAS);
    }

    public boolean objetivoAlcancado() {
        return CAMPOS.stream().allMatch(Campo::objetivoAlcancado);
    }

    public void reiniciar() {
        CAMPOS.forEach(Campo::reiniciar);
        sortearMinas();
    }

    @Override
    public void eventoOcorreu(Campo campo, CampoEvento evento) {
        if (evento == CampoEvento.EXPLODIR) {
            mostrarMinas();
            notificarObservadores(false);
        } else if (objetivoAlcancado()) {
            System.out.println("Ganhou");
            notificarObservadores(true);
        }
    }

    private void mostrarMinas() {
        CAMPOS.stream()
                .filter(Campo::isMinado)
                .filter(c -> !c.isMarcado())
                .forEach(c -> c.setAberto(true));
    }
}
