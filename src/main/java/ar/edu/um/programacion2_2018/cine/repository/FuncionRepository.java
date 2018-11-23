package ar.edu.um.programacion2_2018.cine.repository;

import ar.edu.um.programacion2_2018.cine.domain.Funcion;
import ar.edu.um.programacion2_2018.cine.domain.Pelicula;
import ar.edu.um.programacion2_2018.cine.domain.Sala;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Funcion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FuncionRepository extends JpaRepository<Funcion, Long> {

    List<Funcion> findAllByPelicula(Pelicula peliculas);
    Funcion findBySala(Sala sala);
}
