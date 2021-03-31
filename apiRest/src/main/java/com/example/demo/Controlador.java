package com.example.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RequestMapping("/api")
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class Controlador {

    @Autowired
    private ManejadorDeArchivosCSV archivoCSV;

    public Controlador() {
        this.archivoCSV = new ManejadorDeArchivosCSV();
    }

    @GetMapping("/")
    public String index() {
        return "inicio";
    }

    @RequestMapping(value = "/datos/csv",method = RequestMethod.GET)
    public ResponseEntity<Object> enviarDatosCSV(){
        HashMap<String,Object> valores = new HashMap<>();
        valores.put("datos",archivoCSV.obtenerListaDeEstaciones());
        return new ResponseEntity<>(valores,HttpStatus.OK);
    }

    @RequestMapping(value = "/datos/json",method = RequestMethod.GET)
    public ResponseEntity<Object> enviarDatosJson(){
        HashMap<String,Object> valores = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        ArrayList provincias = new ArrayList();
        try {
            provincias = mapper.readValue(new File("src/main/resources/files/provincias.json"), ArrayList.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        valores.put("provincias",provincias);
        return new ResponseEntity<>(valores,HttpStatus.OK);
    }
}

