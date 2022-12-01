package com.example.phenlineadialer.models;

public class GenusuarioTelefonos {
    private int id;
    private int idUsuario;
    private String nombreCompleto;
    private String email;
    private int prioridad;
    private String telefono;
    private Boolean administrador;
    private String tipo;

    public GenusuarioTelefonos() {
    }

    public GenusuarioTelefonos(int id, int idUsuario, String nombreCompleto, String email, int prioridad, String telefono, Boolean administrador, String tipo) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.prioridad = prioridad;
        this.telefono = telefono;
        this.administrador = administrador;
        this.tipo = tipo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
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

    public Boolean getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Boolean administrador) {
        this.administrador = administrador;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
