package com.example.proyectointegrador.models;

public class Nota {
    private String correcto, incorrecto;

    public Nota( String correcto, String incorrecto) {
        this.correcto = correcto;
        this.incorrecto = incorrecto;
    }

    public String getCorrecto() {
        return correcto;
    }

    public void setCorrecto(String correcto) {
        this.correcto = correcto;
    }

    public String getIncorrecto() {
        return incorrecto;
    }

    public void setIncorrecto(String incorrecto) {
        this.incorrecto = incorrecto;
    }
}
