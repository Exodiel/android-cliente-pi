package com.example.proyectointegrador.models;

public class Materia {
    private int id_mat;
    private String nombre, imagen;
    private double total;

    public Materia(int id_mat, String nombre, String imagen, double total) {
        this.id_mat = id_mat;
        this.nombre = nombre;
        this.imagen = imagen;
        this.total = total;
    }

    public int getId_mat() {
        return id_mat;
    }

    public void setId_mat(int id_mat) {
        this.id_mat = id_mat;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
