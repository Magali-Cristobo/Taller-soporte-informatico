package com.example.demo;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

@Service
public class ManejadorDeArchivosCSV {

    private String ubicacionArchivo;
    private String nombreDeArchivo;
    private char separador;

    public ManejadorDeArchivosCSV() {
        this.ubicacionArchivo = "src/main/resources/files/";
        this.nombreDeArchivo = "estaciones-de-subte.csv";
        this.separador = ',';
    }

    public CSVParser formatearSeparador(){
        CSVParserBuilder puntoYcomaBuilder = new CSVParserBuilder();
        puntoYcomaBuilder = puntoYcomaBuilder.withSeparator(separador);
        CSVParser parser = puntoYcomaBuilder.build();
        return parser;
    }

    public FileReader obtenerArchivo(){
        FileReader reader = null;
        try {
            reader = new FileReader(ubicacionArchivo + nombreDeArchivo);
        } catch (FileNotFoundException e) {
            String mensaje = "Archivo no encontrado, verifique el nombre y/o la ubicaión del mismo";
            System.out.println(mensaje);
            e.printStackTrace();
        }
        return reader;
    }

    public CSVReader obtenerArchivoFormateado(){
        FileReader fileReader = this.obtenerArchivo();
        CSVReaderBuilder readerBuilder = new CSVReaderBuilder(fileReader);
        CSVParser separadorFormateado = this.formatearSeparador();
        readerBuilder = readerBuilder.withCSVParser(separadorFormateado);
        CSVReader reader = readerBuilder.build();
        return reader;
    }

    public Iterator<String[]> obtenerIterador(){
        CSVReader reader = this.obtenerArchivoFormateado();
        Iterator<String[]> iterador = reader.iterator();
        return iterador;
    }

    public void imprimirDatosConEspacios() throws FileNotFoundException {

        CSVReader reader = this.obtenerArchivoFormateado();
        Iterator<String[]> iterador = reader.iterator();

        while (iterador.hasNext()){
            String[] fila = iterador.next();
            String filaInfo = fila[0] + " " + fila[1] + " " + fila[2] + " " + fila[3] + " " + fila[5];
            System.out.println(filaInfo);

        }

    }

    public static void main(String[] args) {

    }

}
