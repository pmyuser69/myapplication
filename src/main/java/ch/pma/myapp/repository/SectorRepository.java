package ch.pma.myapp.repository;

import ch.pma.myapp.domain.Sector;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Sector entity.
 */
public interface SectorRepository extends JpaRepository<Sector,Long> {

}
