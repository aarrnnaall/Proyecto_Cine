package ar.edu.um.programacion2_2018.cine.repository;

import ar.edu.um.programacion2_2018.cine.domain.Butaca;
import ar.edu.um.programacion2_2018.cine.domain.Sala;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data  repository for the Butaca entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ButacaRepository extends JpaRepository<Butaca, Long> {
    List<Butaca> findAllBySala(Sala sala_desc);



    List<Butaca> findAllByIdNotIn(Iterable<Long> longs);

    //List<Butaca> findAllByIdIsNot(Long id);

}
