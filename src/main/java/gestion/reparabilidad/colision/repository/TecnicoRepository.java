package gestion.reparabilidad.colision.repository;

import gestion.reparabilidad.colision.modelo.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
}
