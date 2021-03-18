package com.example.demo;

public class Estacion {
    private float longitud;
    private float latitud;
    private int id;
    private String nombre;
    private String linea;

    public Estacion(float longitud, float latitud, int id, String nombre, String linea ){
        this.longitud=longitud;
        this.latitud=latitud;
        this.id=id;
        this.nombre=nombre;
        this.linea=linea;
    }

    public String getLinea() {
        return linea;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLatitud(float latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(float longitud) {
        this.longitud = longitud;
    }

    public void setLinea(String linea) {
        this.linea = linea;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
