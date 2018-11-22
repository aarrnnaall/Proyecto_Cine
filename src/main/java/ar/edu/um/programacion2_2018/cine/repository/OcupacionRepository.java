package ar.edu.um.programacion2_2018.cine.repository;

import ar.edu.um.programacion2_2018.cine.domain.Ocupacion;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;


/**
 * Spring Data  repository for the Ocupacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OcupacionRepository extends JpaRepository<Ocupacion, Long> {

}
