package ar.edu.um.programacion2_2018.cine.repository;

import ar.edu.um.programacion2_2018.cine.domain.Butaca;
import ar.edu.um.programacion2_2018.cine.domain.Funcion;
import ar.edu.um.programacion2_2018.cine.domain.Ocupacion;
import ar.edu.um.programacion2_2018.cine.domain.Ticket;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Spring Data  repository for the Ocupacion entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OcupacionRepository extends JpaRepository<Ocupacion, Long> {

    //List<Butaca> findOcupacionByFuncionAndButacaNotNull(Funcion funcion);
    List<Ocupacion> findAllByFuncionAndButacaNotNull(Funcion funcion);
    Ocupacion findByTicket(Ticket ticket);

    Ocupacion findByButaca(Optional<Butaca> butaca);
}
