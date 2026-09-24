package gestion.reparabilidad.colision.repository;

import gestion.reparabilidad.colision.modelo.Tecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TecnicoRepository extends JpaRepository<Tecnico, Long> {
}
