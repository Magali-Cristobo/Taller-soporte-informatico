package com.example.demo;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        //https://api.mongodb.com/java/3.2/org/bson/Document.html

        AccesoMongoDB acceso = new AccesoMongoDB();
        acceso.conectarABaseDeDatos("personas");
        acceso.conectarAColeccion("socios1");


        Document datosAInsertar = new Document();

        List<Socio> socios = new ArrayList<>();
        socios.add(new Socio("Carlos", 23, "Argentinos", "Casado", "Secundario"));
        socios.add(new Socio("Paul", 23, "Argentinos", "Casado", "Univ"));

        List<Document> listaDeDocumentos = new ArrayList<>();

        for (Socio socio : socios) {

            Document nuevoDocumento = new Document();
            nuevoDocumento.append("nombre", socio.getNombre());
            nuevoDocumento.append("edad", socio.getEdad());
            nuevoDocumento.append("equipo", socio.getEquipo());
            nuevoDocumento.append("estadoCivil", socio.getEstadoCivil());
            nuevoDocumento.append("nivelDeEstudios", socio.getNivelDeEstudios());
            listaDeDocumentos.add(nuevoDocumento);

        }


        HashMap<String, Object> map = new HashMap<>();
        map.put("Dato1", 1);
        map.put("Dato2", 2);
        map.put("Dato3", 3);

        Document nuevoDocument = new Document(map);

        datosAInsertar.append("listaDeSocios", listaDeDocumentos);
        datosAInsertar.append("dato", 5);
        datosAInsertar.append("map", map);

        //acceso.getColeccion().insertOne(datosAInsertar);

        HashMap<String, Object> mapConMap = new HashMap<>();

        HashMap<String, Object> submap = new HashMap<>();
        submap.put("submap1", 1);
        submap.put("submap2", 2);

        mapConMap.put("submap", submap);

        Document mapConMapDocument = new Document(mapConMap);

        //acceso.getColeccion().insertOne(mapConMapDocument);

        Bson documentAEliminar = mapConMapDocument;
        acceso.getColeccion().deleteOne(mapConMapDocument);


        FindIterable resultado = acceso.getColeccion().find();

        MongoCursor iterador = resultado.iterator();

        while (iterador.hasNext()) {

            Document documento = (Document) iterador.next();

            List<Document> listDoc = (List<Document>) documento.get("listaDeSocios");

            for (Document document : listDoc) {

                String nombre = document.getString("nombre");
                int edad = document.getInteger("edad");
                String equipo = document.getString("equipo");
                String estadoCivil = document.getString("estadoCivil");
                String nivelDeEstudios = document.getString("nivelDeEstudios");

                Socio nuevoSocio = new Socio(nombre,edad,equipo,estadoCivil,nivelDeEstudios);

            }

            Document mapDoc = (Document) documento.get("map");
            int valorDeDato1 = mapDoc.getInteger("Dato1");

            System.out.println(valorDeDato1);


/*



            Socio socioEncontrado = new Socio(nombre, edad, equipo, estadoCivil, nivelDeEstudios);

            sociosBuscados.add(socioEncontrado);


 */
            //System.out.println(listDoc);
            //System.out.println(mapDoc);
        }
    }
}
