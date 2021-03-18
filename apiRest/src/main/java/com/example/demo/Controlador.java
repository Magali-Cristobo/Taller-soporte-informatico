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
        valores.put("provincias",provincias);
        return new ResponseEntity<>(valores,HttpStatus.OK);
    }

    @PostMapping("procesar")
    public ResponseEntity<Object>uploadFile(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Seleccione un archivo",HttpStatus.NOT_ACCEPTABLE);
        }else {
            try {
                procesarArchivo(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Map<String,Object> datos = this.obtenerInformacionSolicitada();
        return new ResponseEntity<>(datos, HttpStatus.OK);
    }

    public void procesarArchivo(MultipartFile archivo) throws IOException {
        servicioParaSubirArchivos.guardarArchivo(archivo);
        this.guardarDatosALaBase();
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

    public void guardarDatosALaBase(){
        List<Socio> sociosList = this.obtenerListaDeSocios();
        accesoABaseDeDatos.insertarSocios(sociosList);
    }

    public HashMap<String,Long> obtenerEquipoConCantidadDeSocios(){

        HashMap<String,Long> equiposConCantidadDeSocios = new HashMap<>();
        HashSet<String> equipos = this.accesoABaseDeDatos.obtenerEquipos();

        for (String nombreDeEquipo : equipos) {
            long cantidadDeSocios = this.obtenerCantidadDeSocios(nombreDeEquipo);
            equiposConCantidadDeSocios.put(nombreDeEquipo,cantidadDeSocios);
        }
        System.out.println(equiposConCantidadDeSocios);
        return equiposConCantidadDeSocios;
    }

    public List<String> obtenerEquiposDeMayorAMenorSegunSocios(){

        HashMap<String,Long> equiposConSocios = this.obtenerEquipoConCantidadDeSocios();

        List<Long> cantidadesDeSocios = new ArrayList<>(equiposConSocios.values());
        Collections.sort(cantidadesDeSocios);

        List<String> nombresDeEquipos = new ArrayList<>();

        for (int i = cantidadesDeSocios.size() - 1; i >= 0; i--) {
            for (Map.Entry<String,Long> elemento : equiposConSocios.entrySet()){
                if (elemento.getValue() == cantidadesDeSocios.get(i)){
                    nombresDeEquipos.add(elemento.getKey());
                }
            }
        }

        System.out.println("equiposOrdenados" + nombresDeEquipos);
        return nombresDeEquipos;
    }

    public long obtenerCantidadTotalDeRegistros(){
        long cantidadTotal = accesoABaseDeDatos.obtenerCantidadDeDocumentos();
        return cantidadTotal;
    }

    public int obtenerCantidadDeSocios(String nombreDeEquipo){
        List<Socio> sociosDeEquipo = this.obtenerSociosDeEquipo(nombreDeEquipo);
        int cantidadDeSocios = sociosDeEquipo.size();
        return cantidadDeSocios;
    }

    public List<Socio> obtenerSociosDeEquipo(String nombreDeEquipo){

        HashMap<String,String> filtro = new HashMap<>();
        filtro.put("equipo",nombreDeEquipo);
        List<Socio> sociosDeEquipo = accesoABaseDeDatos.obtenerSociosCon(filtro);

        return sociosDeEquipo;
    }

    public int obtenerMenorEdadRegistradaPorEquipo(String nombreDeEquipo){
        List<Socio> socios = this.obtenerSociosDeEquipo(nombreDeEquipo);
        socios = Lista.ordenarMenorAMayorSegunEdad(socios);
        int menorEdadRegistrada = socios.get(0).getEdad();
        return menorEdadRegistrada;
    }

    public int obtenerMayorEdadRegistradaPorEquipo(String nombreDeEquipo){
        List<Socio> socios = this.obtenerSociosDeEquipo(nombreDeEquipo);
        socios = Lista.ordenarMayorAMenorSegunEdad(socios);
        int menorEdadRegistrada = socios.get(0).getEdad();
        return menorEdadRegistrada;
    }

    public float obtenerEdadPromedioDeSocios(String nombreDeEquipo){

        List<Socio> sociosDeEquipo = this.obtenerSociosDeEquipo(nombreDeEquipo);
        float edadPromedio = this.obtenerEdadPromedio(sociosDeEquipo);

        return edadPromedio;
    }

    public float obtenerEdadPromedio(List<Socio> socios){

        long cantidadDeSocios = socios.size();
        long sumaTotalDeEdades = 0;

        for (Socio socio : socios) {
            sumaTotalDeEdades += socio.getEdad();
        }

        float edadPromedio = sumaTotalDeEdades / cantidadDeSocios;
        return edadPromedio;
    }



    public List<HashMap<String,Object>> obtenerCienPrimerosSociosCasadosUniversitarios(){

        HashMap<String,String> requisitos = new HashMap<>();
        requisitos.put("estadoCivil","Casado");
        requisitos.put("nivelDeEstudios","Universitario");

        List<Socio> sociosBuscados = accesoABaseDeDatos.obtenerSociosCon(requisitos);
        sociosBuscados = Lista.ordenarMayorAMenorSegunEdad(sociosBuscados);

        List<Socio> cienPrimeros = new ArrayList<>();

        if (sociosBuscados.size() >=100 ) {
            cienPrimeros = sociosBuscados.subList(0, 100);
        } else {
            cienPrimeros = sociosBuscados;
        }

        List<HashMap<String,Object>> datos = new ArrayList<>();

        for (Socio socio : cienPrimeros) {
            HashMap<String,Object> datosDeSocio = new HashMap<>();
            datosDeSocio.put("nombre",socio.getNombre());
            datosDeSocio.put("edad",socio.getEdad());
            datosDeSocio.put("equipo",socio.getEquipo());
            datos.add(datosDeSocio);
        }

        return datos;
    }

    public List<String> obtenerCincoNombresMasComunes(String nombreDeEquipo){

        HashMap<String,String> requisitos = new HashMap<>();
        requisitos.put("equipo",nombreDeEquipo);

        List<Socio> sociosBuscados = accesoABaseDeDatos.obtenerSociosCon(requisitos);
        List<String> nombresDeSociosBuscados = Lista.obtenerNombresDeSocios(sociosBuscados);

        SortedMap aparicionesSegunNombre = new TreeMap(java.util.Collections.reverseOrder());

        for (String nombre : nombresDeSociosBuscados) {
            int apariciones = Collections.frequency(nombresDeSociosBuscados,nombre);
            aparicionesSegunNombre.put(nombre,apariciones);
        }

        nombresDeSociosBuscados = new ArrayList<>(aparicionesSegunNombre.keySet());
        nombresDeSociosBuscados = nombresDeSociosBuscados.subList(0,5);

        return nombresDeSociosBuscados;
    }

    public List<HashMap<String,Object>> obtenerInfoGeneralDeEquipos(){

        List<HashMap<String,Object>> equipos = new ArrayList<>();
        List<String> equiposRegistrados = this.obtenerEquiposDeMayorAMenorSegunSocios();

        for (String nombreEquipo : equiposRegistrados) {

            HashMap<String,Object> infoEquipo = new HashMap<>();
            infoEquipo.put("nombre",nombreEquipo);

            int cantidadDeSocios = this.obtenerCantidadDeSocios(nombreEquipo);
            infoEquipo.put("cantidadDeSocios",cantidadDeSocios);

            float edadPromedio = this.obtenerEdadPromedioDeSocios(nombreEquipo);
            infoEquipo.put("edadPromedio",edadPromedio);

            int menorEdadRegistrada = this.obtenerMenorEdadRegistradaPorEquipo(nombreEquipo);
            infoEquipo.put("menorEdadRegistrada",menorEdadRegistrada);

            int mayorEdadRegistrada = this.obtenerMayorEdadRegistradaPorEquipo(nombreEquipo);
            infoEquipo.put("mayorEdadRegistrada",mayorEdadRegistrada);

            equipos.add(infoEquipo);

        }

        return equipos;
    }

    public Map<String,Object> obtenerInformacionSolicitada(){

        Map<String,Object> informacion = new HashMap<>();

        informacion.put("cantidadTotalDePersonasRegistradas",this.obtenerCantidadTotalDeRegistros());
        informacion.put("edadPromedioDeSociosDeRacing",this.obtenerEdadPromedioDeSocios("Racing"));
        informacion.put("cienPrimerasPersonas",this.obtenerCienPrimerosSociosCasadosUniversitarios());
        informacion.put("cincoNombresMasComunesDeRiver",this.obtenerCincoNombresMasComunes("River"));
        informacion.put("listaDeEquiposSegunCantidadDeSocios",this.obtenerInfoGeneralDeEquipos());

        return informacion;
    }



}

