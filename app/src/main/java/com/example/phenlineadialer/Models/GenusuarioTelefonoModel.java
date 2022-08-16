package com.example.phenlineadialer.Models;

public class GenusuarioTelefonoModel {


    private String nombreProp;
    private int idUsuario;
    private String nombreTelefono;
    private String email;
    private int prioridad;
    private String telefono;

    public GenusuarioTelefonoModel() {

    }

    public GenusuarioTelefonoModel(String nombreProp, int idUsuario, String nombreTelefono, String email, int prioridad, String telefono) {
        this.nombreProp = nombreProp;
        this.idUsuario = idUsuario;
        this.nombreTelefono = nombreTelefono;
        this.email = email;
        this.prioridad = prioridad;
        this.telefono = telefono;
    }

    public String getNombreProp() {
        return nombreProp;
    }

    public void setNombreProp(String nombreProp) {
        this.nombreProp = nombreProp;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreTelefono() {
        return nombreTelefono;
    }

    public void setNombreTelefono(String nombreTelefono) {
        this.nombreTelefono = nombreTelefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}
