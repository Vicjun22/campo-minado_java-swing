package br.com.vicjun22.campominado.model;

import java.util.ArrayList;
import java.util.List;

public class Campo {

    private final int LINHA;
    private final int COLUNA;

    private boolean aberto = false;
    private boolean minado = false;
    private boolean marcado = false;

    private final List<Campo> VIZINHOS = new ArrayList<>();
    private final List<CampoObservador> observadores = new ArrayList<>();

    public Campo(int linha, int coluna) {
        this.LINHA = linha;
        this.COLUNA = coluna;
    }

    public void registrarObservador(CampoObservador observador) {
        observadores.add(observador);
    }

    private void notificarObservadores(CampoEvento evento) {
        observadores.forEach(observador -> observador.eventoOcorreu(this, evento));
    }

    boolean adicionarVizinho(Campo vizinho) {
        boolean linhaDiferente = LINHA != vizinho.LINHA;
        boolean colunaDiferente = COLUNA != vizinho.COLUNA;
        boolean diagonal = linhaDiferente && colunaDiferente;

        int deltaLinha = Math.abs(LINHA - vizinho.LINHA);
        int deltaColuna = Math.abs(COLUNA - vizinho.COLUNA);
        int deltaGeral = deltaColuna + deltaLinha;

        if (deltaGeral == 1 && !diagonal) {
            VIZINHOS.add(vizinho);
            return true;
        }
        else if (deltaGeral == 2 && diagonal) {
            VIZINHOS.add(vizinho);
            return true;
        } else {
            return false;
        }
    }

    public void alternarMarcacao() {
        if (!aberto) {
            marcado = !marcado;

            if (marcado) {
                notificarObservadores(CampoEvento.MARCAR);
            } else {
                notificarObservadores(CampoEvento.DESMARCAR);
            }
        }
    }

    public boolean abrir() {
        if (!aberto && !marcado) {
            if (minado) {
                notificarObservadores(CampoEvento.EXPLODIR);
                return true;
            }

            setAberto(true);
            notificarObservadores(CampoEvento.ABRIR);

            if (vizinhancaSegura()) VIZINHOS.forEach(Campo::abrir);

            return true;
        } else return false;
    }

    public boolean vizinhancaSegura() {
        return VIZINHOS.stream().noneMatch(v -> v.minado);
    }

    void minar() {
        minado = true;
    }

    boolean objetivoAlcancado() {
        boolean desvendado = !minado && aberto;
        boolean protegido = minado && marcado;
        return desvendado || protegido;
    }

    public int minasNaVizinhanca() {
        return (int) VIZINHOS.stream().filter(v -> v.minado).count();
    }

    void reiniciar() {
        aberto = false;
        minado = false;
        marcado = false;
        notificarObservadores(CampoEvento.REINICIAR);
    }

    public boolean isMinado() {
        return minado;
    }

    public boolean isMarcado() {
        return marcado;
    }

    void setAberto(boolean aberto) {
        this.aberto = aberto;
        if (aberto) notificarObservadores(CampoEvento.ABRIR);
    }
}
