package com.example.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Lista<E> extends ArrayList<E> {

    public static List<Socio> ordenarMenorAMayorSegunEdad(List<Socio> socios){

        Collections.sort(socios, new Comparator<Socio>() {
            @Override
            public int compare(Socio socio1, Socio socio2) {
                Integer edadSocio1 = socio1.getEdad();
                Integer edadSocio2 = socio2.getEdad();
                int resultadoDeComparacion = edadSocio1.compareTo(edadSocio2);
                return resultadoDeComparacion;
            }
        });

        return socios;

    }

    public static List<Socio> ordenarMayorAMenorSegunEdad(List<Socio> socios){

        Collections.sort(socios, new Comparator<Socio>() {
            @Override
            public int compare(Socio socio1, Socio socio2) {
                Integer edadSocio1 = socio1.getEdad();
                Integer edadSocio2 = socio2.getEdad();
                int resultadoDeComparacion = edadSocio2.compareTo(edadSocio1);
                return resultadoDeComparacion;
            }
        });

        return socios;

    }

    public static List<String> obtenerNombresDeSocios(List<Socio> socios){

        List<String> nombres = new ArrayList<>();

        for (Socio socio : socios) {
            nombres.add(socio.getNombre());
        }

        return nombres;
    }






}
