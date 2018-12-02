package ar.edu.um.programacion2_2018.cine.web.rest;
import ar.edu.um.programacion2_2018.cine.domain.*;
import ar.edu.um.programacion2_2018.cine.repository.*;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@RestController
@RequestMapping("/api")
class CineResourse {
    private final Logger log = LoggerFactory.getLogger(FuncionResource.class);
    @Autowired
    private PeliculaRepository peliculaRepository;
    @Autowired
    private FuncionRepository funcionRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private ButacaRepository butacaRepository;
    @Autowired
    private OcupacionRepository ocupacionRepository;
    @Autowired
    private EntradaRepository entradaRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private ClienteRepository clienteRepository;

    private static final String ENTITY_NAME = "ocupacion";


    //Metodos Agregados
    @GetMapping("/funcions/peliculas/{titulo}/")//Buscar la Funcion de una Pelicula (Le pasas el titulo de la Pelicula)
    @Timed
    public List<Funcion> getFuncionPelicula(@PathVariable String titulo) {
        log.debug("REST request to get Funcion : {}", titulo);
        Pelicula pelicula = peliculaRepository.findByTitulo(titulo);

        List<Funcion> funciones = funcionRepository.findAllByPelicula(pelicula);
        return funciones;
    }

    @GetMapping("/sala/butacas_disponibles/{desc_sala}/{id_funcion}")//Ver las butacas libre de una Sala (Le Pasamos la descripcion de la Sala)
    @Timed
    public List<Butaca> getSalaButacas(@PathVariable String desc_sala,@PathVariable Long id_funcion) {
        log.debug("REST request to get Sala : {}", desc_sala);
        Sala sala_desc = salaRepository.findByDescripcion(desc_sala);
        Funcion funcion = funcionRepository.findBySalaAndId(sala_desc,id_funcion);

        List<Ocupacion> ocupacions= ocupacionRepository.findAllByFuncionAndButacaNotNull(funcion);

        Iterable<Long> butacas_id = new ArrayList<>();

        for(int indice = 0;indice<ocupacions.size();indice++)
        {
            ((ArrayList<Long>) butacas_id).add(ocupacions.get(indice).getButaca().getId());
        }

        if(ocupacions == null || ocupacions.isEmpty()){
            return butacaRepository.findAllBySala(sala_desc);
        }else   {
            List<Butaca> butacas_disponible = butacaRepository.findAllByIdNotInAndSala(butacas_id,sala_desc);
            return butacas_disponible;

        }




    }

    @PostMapping("/ocupacions/butaca/{id_butacas}/{desc_sala}/{id_funcion}")//Revervar butaca disponible
    @Timed
    public List<Ocupacion> createOcupacion(@PathVariable String id_butacas, @PathVariable String desc_sala, @PathVariable Long id_funcion) throws URISyntaxException   {

        log.debug("REST request to save Ocupacion : {}", id_butacas);
        Sala sala_desc = salaRepository.findByDescripcion(desc_sala);
        Funcion funcion= funcionRepository.findBySalaAndId(sala_desc,id_funcion);
        Calendar fecha = Calendar.getInstance();
        ZonedDateTime fecha_actual = ZonedDateTime.now();
        String[] butacas = id_butacas.split("-");
        List<Ocupacion> ocupacions= new ArrayList<>();
        Iterable<Long> butacas_id = new ArrayList<>();

        for(int indice = 0;indice<butacas.length;indice++)
        {
            ((ArrayList<Long>) butacas_id).add(Long.parseLong(butacas[indice]));
        }

        List<Butaca> butacas_disponible = butacaRepository.findAllById(butacas_id);

        for(int indice = 0;indice<butacas_disponible.size();indice++)
        {
            Ocupacion ocupacion=new Ocupacion();
            ocupacion.setButaca(butacas_disponible.get(indice));
            ocupacion.setFuncion(funcion);
            ocupacion.setValor(funcion.getValor());
            ocupacion.setCreated(fecha_actual);
            ocupacion.setUpdated(fecha_actual);
            ocupacions.add(ocupacion);
        }

        List<Ocupacion> result_ocupacion = ocupacionRepository.saveAll(ocupacions);

        return result_ocupacion;
    }

    @PostMapping("/tickets/butaca/{id_cliente}/{cant_butaca}/{id_butacas}/{num_tarjeta}")//crear ticket consumiendo rest de validacion de la parte de pago
    @Timed
    public String createTicketWintOcupacion(@PathVariable Long id_cliente,@PathVariable Integer cant_butaca,@PathVariable String id_butacas,@PathVariable String num_tarjeta)throws IOException, java.io.IOException {
        log.debug("REST request to save Ticket : {}", id_butacas);
        LocalDate fecha_actual = LocalDate.now();
        ZonedDateTime fecha_actual2=ZonedDateTime.now();

        String[] butacas = id_butacas.split("-");
        Iterable<Long> butacas_id = new ArrayList<>();
        List<Ocupacion> ocupacions= new ArrayList<>();
        Ticket ticket_cargado= new Ticket();
        for(int indice = 0;indice<butacas.length;indice++)
        {
            ((ArrayList<Long>) butacas_id).add(Long.parseLong(butacas[indice]));
        }

        List<Butaca> butacas_disponible = butacaRepository.findAllById(butacas_id);

        for(int indice = 0;indice<butacas.length;indice++)
        {
            ocupacions.add(ocupacionRepository.findByButaca(butacas_disponible.get(indice)));
        }

        ticket_cargado.setFechaTransaccion(fecha_actual);
        ticket_cargado.setCreated(fecha_actual2);
        ticket_cargado.setUpdated(fecha_actual2);
        ticket_cargado.setButacas(cant_butaca);
        BigDecimal cant_but = new BigDecimal(cant_butaca);
        BigDecimal cant_valor = ocupacions.get(0).getValor();
        BigDecimal importe;
        importe = cant_but.multiply(cant_valor);
        URL url = new URL("http://localhost:8081/api/validacion/"+num_tarjeta+"/"+importe);//your url i.e fetch data from .
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1dGgiOiJST0xFX0FETUlOLFJPTEVfVVNFUiIsImV4cCI6MTU0Mzc5MjEwOH0.vDAok7c3NRsddjsMi6t51jeJtxDPM_cLGHXCU4Dh2zWU8Dm8k3R2SCVzeMAZmQsXZOuQ8nYOcr4FbzIgtO8Rjw");
        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP Error code : "
                + conn.getResponseCode());
        }
        InputStreamReader in = new InputStreamReader(conn.getInputStream());
        BufferedReader br = new BufferedReader(in);
        String output;
        output = br.readLine();
        ticket_cargado.setImporte(importe);
        Optional<Cliente> cliente_ticket=clienteRepository.findById(id_cliente);
        ticket_cargado.setCliente(cliente_ticket.get());
        final String uuid = UUID.randomUUID().toString();
        ticket_cargado.setPagoUuid(output);
        String salida="";
        if(output!= null) {
            Ticket result_ticket = ticketRepository.save(ticket_cargado);

            for(int indice = 0;indice<butacas.length;indice++)
            {
                ocupacions.get(indice).setTicket(result_ticket);
                ocupacionRepository.save(ocupacions.get(indice));
            }
            salida= "Operacion Completa";
        }
        else {

            ocupacionRepository.deleteAll(ocupacions);
            salida ="Saldo Insuficiente";
        }

        return salida ;


    }

    @PostMapping("/cargar_entradas")//Carga Entradas
    @Timed
    public List<Entrada> createdEntradas() {
        log.debug("REST request to get Entradas : {}");
        ZonedDateTime fecha_actual2=ZonedDateTime.now();
        List<Ocupacion> ocupacions= ocupacionRepository.findAllByTicketNotNullAndEntradaNull();
        List<Entrada> entradas_acrear = new ArrayList<>();
        for(int indice = 0;indice<ocupacions.size();indice++)
        {
            Entrada entrada = new Entrada();
            entrada.setValor(ocupacions.get(indice).getValor());
            entrada.setUpdated(fecha_actual2);
            entrada.setCreated(fecha_actual2);
            entrada.setDescripcion(ocupacions.get(indice).getButaca().getFila()+"-"+ocupacions.get(indice).getButaca().getNumero()+"__"+ocupacions.get(indice).getFuncion().getSala().getDescripcion());
            entradas_acrear.add(entrada);
        }
        List<Entrada> result_entradas = entradaRepository.saveAll(entradas_acrear);
        for(int indice = 0;indice<ocupacions.size();indice++)
        {
            ocupacions.get(indice).setEntrada(entradas_acrear.get(indice));
        }
        ocupacionRepository.saveAll(ocupacions);

        return result_entradas;



    }

    @PostMapping("/cargar/butacas/{desc_sala}")//Carga 224 butacas de una sala especifica
    @Timed
    public String createdButacas(@PathVariable String desc_sala) {
        log.debug("REST request to get Butacas : {}");
        ZonedDateTime fecha_actual2=ZonedDateTime.now();
        Sala sala_butaca=salaRepository.findByDescripcion(desc_sala);

        List<Butaca> butacas_acargar = new ArrayList<>();
        int cont=0;
        for(char fila = 'A'; fila <= 'P' ;fila++)

        {
            for(int numero = 1;numero<=14;numero++)
            {
            Butaca butaca=new Butaca();
            butaca.setFila(String.valueOf(fila));
            butaca.setNumero(numero);
            cont++;
            if((cont) <= 70) {
                butaca.setDescripcion("Fila de Adelante");
            }
            if((cont) >= 70 && (cont) <= 140){
                butaca.setDescripcion("Fila del Medio");
            }
            if((cont) >= 140 ){
                butaca.setDescripcion("Fila de Atras");
            }
            butaca.setCreated(fecha_actual2);
            butaca.setUpdated(fecha_actual2);
            butaca.setSala(sala_butaca);

            butacas_acargar.add(butaca);

            }
        }

        butacaRepository.saveAll(butacas_acargar);

        return "Butacas Cargadas";

    }
    @GetMapping("/consultar_tickets/{id_ticket}")//Consultar Ticket
    @Timed
    public List<Ocupacion> getTicket(@PathVariable Long id_ticket) {
        log.debug("REST request to get Ticket : {}", id_ticket);
        Optional<Ticket> ticket = ticketRepository.findById(id_ticket);
        List<Ocupacion> ocupacions=ocupacionRepository.findAllByTicket(ticket.get());
        return ocupacions;
    }


}
