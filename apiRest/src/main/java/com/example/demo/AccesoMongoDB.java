package com.example.demo;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

@Service
public class AccesoMongoDB {

    private MongoDatabase baseDeDatos;
    private MongoCollection coleccion;
    private String host;
    private int puerto;

    public AccesoMongoDB() {
        this.host = "localhost";
        this.puerto = 27017;
        this.conectarABaseDeDatos("personas");
        this.conectarAColeccion("socios");
    }

    public MongoDatabase getBaseDeDatos() {
        return baseDeDatos;
    }

    public void setBaseDeDatos(MongoDatabase baseDeDatos) {
        this.baseDeDatos = baseDeDatos;
    }

    public MongoCollection getColeccion() {
        return coleccion;
    }

    public void setColeccion(MongoCollection coleccion) {
        this.coleccion = coleccion;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public void conectarABaseDeDatos(String nombreBaseDeDatos){
        MongoClient mongo = new MongoClient( host,puerto );
        this.baseDeDatos = mongo.getDatabase(nombreBaseDeDatos);
    }

    public void conectarAColeccion(String nombreDeColeccion){
        if (existeLaColeccion(nombreDeColeccion)){
            this.coleccion = baseDeDatos.getCollection(nombreDeColeccion);
        } else {
            baseDeDatos.createCollection(nombreDeColeccion);
            this.coleccion = baseDeDatos.getCollection(nombreDeColeccion);
        }
    }

    public long obtenerCantidadDeDocumentos(){
        long cantidadDeRegistros = coleccion.count();
        return cantidadDeRegistros;
    }

    public void insertarSocios(List<Socio> socios){

        List<Document> documentosAIntertar = new ArrayList<>();

        for (Socio socio : socios) {

            Document nuevoDocumento = new Document();
            nuevoDocumento.append("nombre",socio.getNombre());
            nuevoDocumento.append("edad",socio.getEdad());
            nuevoDocumento.append("equipo",socio.getEquipo());
            nuevoDocumento.append("estadoCivil",socio.getEstadoCivil());
            nuevoDocumento.append("nivelDeEstudios",socio.getNivelDeEstudios());
            documentosAIntertar.add(nuevoDocumento);

        }

        coleccion.insertMany(documentosAIntertar);
    }

    public boolean existeLaColeccion(String nombreDeColeccion){

        MongoIterable<String> nombresDeColecciones = baseDeDatos.listCollectionNames();
        boolean existe = false;

        for (String nombre : nombresDeColecciones) {
            if (nombre.equals(nombreDeColeccion)){
                existe = true;
            }
        }
        return existe;
    }


    public List<Socio> obtenerSociosCon(HashMap<String,String> valoresRequeridos) {

        List<Socio> sociosBuscados = new ArrayList<>();

        List<Bson> filtros = new ArrayList<>();

        for (Map.Entry<String,String> elemento : valoresRequeridos.entrySet()){

            Bson equivalencia = Filters.eq(elemento.getKey(),elemento.getValue());
            filtros.add(equivalencia);
        }


        Bson requisitosACumplir = and(filtros);

        FindIterable resultado = coleccion.find(requisitosACumplir);

        MongoCursor iterador = resultado.iterator();

        while (iterador.hasNext()){

            Document documento = (Document) iterador.next();

            String nombre = documento.getString("nombre");
            int edad = documento.getInteger("edad");
            String equipo = documento.getString("equipo");
            String estadoCivil = documento.getString("estadoCivil");
            String nivelDeEstudios = documento.getString("nivelDeEstudios");


            Socio socioEncontrado = new Socio(nombre,edad,equipo,estadoCivil,nivelDeEstudios);

            sociosBuscados.add(socioEncontrado);
        }

        return sociosBuscados;
    }

    public HashSet<String> obtenerEquipos(){

        HashSet<String> equipos = new HashSet<>();
        FindIterable resultado = coleccion.find();
        MongoCursor iterador = resultado.iterator();

        while (iterador.hasNext()){

            Document documento = (Document) iterador.next();
            String equipo = documento.getString("equipo");
            equipos.add(equipo);
        }

        return equipos;
    }

}
