package ar.edu.um.programacion2_2018.cine.web.rest;

import ar.edu.um.programacion2_2018.cine.domain.*;
import ar.edu.um.programacion2_2018.cine.repository.*;
import ar.edu.um.programacion2_2018.cine.web.rest.errors.BadRequestAlertException;
import ar.edu.um.programacion2_2018.cine.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import com.google.errorprone.annotations.FormatString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.*;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Null;
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;

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
    @GetMapping("/funcions/peliculas/{titulo}")//Buscar la Funcion de una Pelicula (Le pasas el titulo de la Pelicula)
    @Timed
    public List<Funcion> getFuncionByPelicula(@PathVariable String titulo) {
        log.debug("REST request to get Funcion : {}", titulo);
        Pelicula pelicula = peliculaRepository.findByTitulo(titulo);

        List<Funcion> funciones = funcionRepository.findAllByPelicula(pelicula);
        return funciones;
    }

    @GetMapping("/sala/butacas/{desc}")//Ver las butacas libre de una Sala (Le Pasamos la descripcion de la Sala)
    @Timed
    public List<Butaca> getSalaButacas(@PathVariable String desc) {
        log.debug("REST request to get Sala : {}", desc);
        Sala sala_desc = salaRepository.findByDescripcion(desc);
        Funcion funcion = funcionRepository.findBySala(sala_desc);

        List<Ocupacion> ocupacions= ocupacionRepository.findAllByFuncionAndButacaNotNull(funcion);
        Iterable<Long> butacas_id = new ArrayList<>();

        for(int indice = 0;indice<ocupacions.size();indice++)
        {
            ((ArrayList<Long>) butacas_id).add(ocupacions.get(indice).getButaca().getId());
        }

        List<Butaca> butacas_disponible = butacaRepository.findAllByIdNotIn(butacas_id);

        return butacas_disponible;



    }

    @PostMapping("/ocupacions/butaca/{id_butacas}/{desc_sala}")//Revervar butaca disponible
    @Timed
    public Ocupacion createOcupacion(@PathVariable Long id_butacas, @PathVariable String desc_sala) throws URISyntaxException   {
        log.debug("REST request to save Ocupacion : {}", id_butacas);
        Ocupacion ocupacion = new Ocupacion();
        Optional<Butaca> butaca_disponible = butacaRepository.findById(id_butacas);
        Sala sala_desc = salaRepository.findByDescripcion(desc_sala);
        Funcion funcion= funcionRepository.findBySala(sala_desc);
        Calendar fecha = Calendar.getInstance();
        ZonedDateTime fecha_actual = ZonedDateTime.now();

        ocupacion.setButaca(butaca_disponible.get());
        ocupacion.setFuncion(funcion);
        ocupacion.setValor(funcion.getValor());
        ocupacion.setCreated(fecha_actual);
        ocupacion.setUpdated(fecha_actual);

        if (ocupacion.getId() != null) {
            throw new BadRequestAlertException("A new ocupacion cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Ocupacion result = ocupacionRepository.save(ocupacion);

        return result;
    }
    @PostMapping("/tickets/butaca/{id_cliente}/{cant_butaca}/{id_butacas}")
    @Timed
    public Ticket createTicketWintOcupacion(@PathVariable Long id_cliente,@PathVariable Integer cant_butaca,@PathVariable String id_butacas) {
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
        ticket_cargado.setImporte(importe);
        Optional<Cliente> cliente_ticket=clienteRepository.findById(id_cliente);
        ticket_cargado.setCliente(cliente_ticket.get());
        final String uuid = UUID.randomUUID().toString();
        ticket_cargado.setPagoUuid(uuid);
        Ticket result_ticket = ticketRepository.save(ticket_cargado);

        for(int indice = 0;indice<butacas.length;indice++)
        {
            ocupacions.get(indice).setTicket(result_ticket);
            ocupacionRepository.save(ocupacions.get(indice));
        }

        return result_ticket;

    }

    @GetMapping("/ticket_NotNull")
    @Timed
    public List<Entrada> getTicket() {
        log.debug("REST request to get Ticket : {}");
        ZonedDateTime fecha_actual2=ZonedDateTime.now();
        List<Ocupacion> ocupacions= ocupacionRepository.findAllByTicketNotNull();
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
        entradaRepository.saveAll(entradas_acrear);
        for(int indice = 0;indice<ocupacions.size();indice++)
        {
            ocupacions.get(indice).setEntrada(entradas_acrear.get(indice));
        }
        ocupacionRepository.saveAll(ocupacions);
        return entradas_acrear;



    }

}
