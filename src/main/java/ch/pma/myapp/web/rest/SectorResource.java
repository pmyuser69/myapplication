package ch.pma.myapp.web.rest;

import com.codahale.metrics.annotation.Timed;
import ch.pma.myapp.domain.Sector;
import ch.pma.myapp.repository.SectorRepository;
import ch.pma.myapp.repository.search.SectorSearchRepository;
import ch.pma.myapp.web.rest.util.HeaderUtil;
import ch.pma.myapp.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Sector.
 */
@RestController
@RequestMapping("/api")
public class SectorResource {

    private final Logger log = LoggerFactory.getLogger(SectorResource.class);
        
    @Inject
    private SectorRepository sectorRepository;
    
    @Inject
    private SectorSearchRepository sectorSearchRepository;
    
    /**
     * POST  /sectors -> Create a new sector.
     */
    @RequestMapping(value = "/sectors",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sector> createSector(@Valid @RequestBody Sector sector) throws URISyntaxException {
        log.debug("REST request to save Sector : {}", sector);
        if (sector.getId() != null) {
            return ResponseEntity.badRequest().headers(HeaderUtil.createFailureAlert("sector", "idexists", "A new sector cannot already have an ID")).body(null);
        }
        Sector result = sectorRepository.save(sector);
        sectorSearchRepository.save(result);
        return ResponseEntity.created(new URI("/api/sectors/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert("sector", result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /sectors -> Updates an existing sector.
     */
    @RequestMapping(value = "/sectors",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sector> updateSector(@Valid @RequestBody Sector sector) throws URISyntaxException {
        log.debug("REST request to update Sector : {}", sector);
        if (sector.getId() == null) {
            return createSector(sector);
        }
        Sector result = sectorRepository.save(sector);
        sectorSearchRepository.save(result);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert("sector", sector.getId().toString()))
            .body(result);
    }

    /**
     * GET  /sectors -> get all the sectors.
     */
    @RequestMapping(value = "/sectors",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Sector>> getAllSectors(Pageable pageable)
        throws URISyntaxException {
        log.debug("REST request to get a page of Sectors");
        Page<Sector> page = sectorRepository.findAll(pageable); 
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sectors");
        return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
    }

    /**
     * GET  /sectors/:id -> get the "id" sector.
     */
    @RequestMapping(value = "/sectors/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Sector> getSector(@PathVariable Long id) {
        log.debug("REST request to get Sector : {}", id);
        Sector sector = sectorRepository.findOne(id);
        return Optional.ofNullable(sector)
            .map(result -> new ResponseEntity<>(
                result,
                HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * DELETE  /sectors/:id -> delete the "id" sector.
     */
    @RequestMapping(value = "/sectors/{id}",
        method = RequestMethod.DELETE,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteSector(@PathVariable Long id) {
        log.debug("REST request to delete Sector : {}", id);
        sectorRepository.delete(id);
        sectorSearchRepository.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert("sector", id.toString())).build();
    }

    /**
     * SEARCH  /_search/sectors/:query -> search for the sector corresponding
     * to the query.
     */
    @RequestMapping(value = "/_search/sectors/{query}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Sector> searchSectors(@PathVariable String query) {
        log.debug("REST request to search Sectors for query {}", query);
        return StreamSupport
            .stream(sectorSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .collect(Collectors.toList());
    }
}
