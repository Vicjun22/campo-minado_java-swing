package br.com.vicjun22.campominado.view;

import br.com.vicjun22.campominado.model.Tabuleiro;

import javax.swing.*;
import java.awt.*;

public class PainelTabuleiro extends JPanel {

    public PainelTabuleiro(Tabuleiro tabuleiro) {

        setLayout(new GridLayout(tabuleiro.getLinhas(), tabuleiro.getColunas()));

        tabuleiro.paraCadaCampo(c -> add(new BotaoCampo(c)));

        tabuleiro.registrarObservador(e -> {
            SwingUtilities.invokeLater(() -> {
                if (e) {
                    JOptionPane.showMessageDialog(this, "Ganhou!");
                } else {
                    JOptionPane.showMessageDialog(this, "Perdeu");
                }

                tabuleiro.reiniciar();
            });
        });
    }
}
