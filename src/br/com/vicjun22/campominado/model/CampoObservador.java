package br.com.vicjun22.campominado.model;

@FunctionalInterface
public interface CampoObservador {

    void eventoOcorreu(Campo campo, CampoEvento evento);

}
