package ar.edu.um.programacion2_2018.cine.web.rest;

import ar.edu.um.programacion2_2018.cine.domain.*;
import ar.edu.um.programacion2_2018.cine.repository.*;
import ar.edu.um.programacion2_2018.cine.web.rest.errors.BadRequestAlertException;
import ar.edu.um.programacion2_2018.cine.web.rest.util.HeaderUtil;
import com.codahale.metrics.annotation.Timed;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Optional;

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
    public Ocupacion createOcupacionWith(@PathVariable Long id_butacas, @PathVariable String desc_sala) throws URISyntaxException   {
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


}
