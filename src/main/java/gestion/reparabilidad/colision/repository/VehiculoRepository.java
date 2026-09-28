package gestion.reparabilidad.colision.repository;

import com.fasterxml.jackson.annotation.JacksonAnnotation;
import gestion.reparabilidad.colision.modelo.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo,Long> {
    List<Vehiculo> findByActivoTrue();
    List<Vehiculo> findByActivoFalse();
}
