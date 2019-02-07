package com.example.proyectointegrador.models;

public class Pregunta {
    private int id_pre,cod_mat;
    private String enunciado,opcion1,opcion2,opcion3,opcion4;

    public Pregunta(int id_pre, int cod_mat, String enunciado, String opcion1, String opcion2, String opcion3, String opcion4) {
        this.id_pre = id_pre;
        this.cod_mat = cod_mat;
        this.enunciado = enunciado;
        this.opcion1 = opcion1;
        this.opcion2 = opcion2;
        this.opcion3 = opcion3;
        this.opcion4 = opcion4;
    }

    public int getId_pre() {
        return id_pre;
    }

    public void setId_pre(int id_pre) {
        this.id_pre = id_pre;
    }

    public int getCod_mat() {
        return cod_mat;
    }

    public void setCod_mat(int cod_mat) {
        this.cod_mat = cod_mat;
    }

    public String getEnunciado() {
        return enunciado;
    }

    public void setEnunciado(String enunciado) {
        this.enunciado = enunciado;
    }

    public String getOpcion1() {
        return opcion1;
    }

    public void setOpcion1(String opcion1) {
        this.opcion1 = opcion1;
    }

    public String getOpcion2() {
        return opcion2;
    }

    public void setOpcion2(String opcion2) {
        this.opcion2 = opcion2;
    }

    public String getOpcion3() {
        return opcion3;
    }

    public void setOpcion3(String opcion3) {
        this.opcion3 = opcion3;
    }

    public String getOpcion4() {
        return opcion4;
    }

    public void setOpcion4(String opcion4) {
        this.opcion4 = opcion4;
    }
}
