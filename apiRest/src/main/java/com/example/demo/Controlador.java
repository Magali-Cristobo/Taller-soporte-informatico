package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequestMapping("/api")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controlador {

    @Autowired
    private AccesoMongoDB accesoABaseDeDatos;

    @Autowired
    private ManejadorDeArchivosCSV archivoCSV;

    @Autowired
    private Servicio servicioParaSubirArchivos;

    public Controlador() {
        this.accesoABaseDeDatos = new AccesoMongoDB();
        this.archivoCSV = new ManejadorDeArchivosCSV();
        this.servicioParaSubirArchivos = new Servicio();
    }

    @GetMapping("/")
    public String index() {
        return "inicio";
    }

    @RequestMapping(value = "/datos/csv",method = RequestMethod.GET)
    public ResponseEntity<Object> enviarDatosCSV(){
        HashMap<String,Object> valores = new HashMap<>();
        valores.put("datos",obtenerListaDeEstaciones());
        return new ResponseEntity<>(valores,HttpStatus.OK);
    }

    @RequestMapping(value = "/datos/json",method = RequestMethod.GET)
    public ResponseEntity<Object> enviarDatosJson() throws IOException {
        HashMap<String,Object> valores = new HashMap<>();
        ObjectMapper mapper2 = new ObjectMapper();
        HashMap provincias = mapper2.readValue(new File("src/main/resources/files/provincias.json"),HashMap.class);
        valores.put("provincias",provincias);//cuando se manda hay un "provincias" de mas
        return new ResponseEntity<>(valores,HttpStatus.OK);
    }

    public List<Socio> obtenerListaDeSocios(){

        List<Socio> sociosList= new ArrayList<>();
        Iterator<String[]> iterador = archivoCSV.obtenerIterador();

        while (iterador.hasNext()){

            String[] fila = iterador.next();
            String nombre = fila[0];
            int edad = Integer.parseInt(fila[1]);
            String equipo = fila[2];
            String estadoCivil = fila[3];
            String nivelDeEstudios = fila[4];

            Socio nuevoSocio = new Socio(nombre,edad,equipo,estadoCivil,nivelDeEstudios);
            sociosList.add(nuevoSocio);

        }

        return sociosList;
    }
    public List<Estacion> obtenerListaDeEstaciones(){

        List<Estacion> listaEstaciones= new ArrayList<>();
        Iterator<String[]> iterador = archivoCSV.obtenerIterador();

        while (iterador.hasNext()){
            String[] fila = iterador.next();
            float longitud = Float.parseFloat(fila[0]);
            float latitud = Float.parseFloat(fila[1]);
            int id = Integer.parseInt(fila[2]);
            String nombre = fila[3];
            String linea = fila[4];
            Estacion nuevaEstacion = new Estacion(longitud,latitud,id,nombre,linea);
            listaEstaciones.add(nuevaEstacion);
        }

        return listaEstaciones;
    }




}

