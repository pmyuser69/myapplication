package ch.pma.myapp.web.rest;

import ch.pma.myapp.Application;
import ch.pma.myapp.domain.Sector;
import ch.pma.myapp.repository.SectorRepository;
import ch.pma.myapp.repository.search.SectorSearchRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.Matchers.hasItem;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/**
 * Test class for the SectorResource REST controller.
 *
 * @see SectorResource
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest
public class SectorResourceIntTest {

    private static final String DEFAULT_NAME = "AAAAA";
    private static final String UPDATED_NAME = "BBBBB";
    private static final String DEFAULT_DESCRIPTION = "AAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBB";

    @Inject
    private SectorRepository sectorRepository;

    @Inject
    private SectorSearchRepository sectorSearchRepository;

    @Inject
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Inject
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    private MockMvc restSectorMockMvc;

    private Sector sector;

    @PostConstruct
    public void setup() {
        MockitoAnnotations.initMocks(this);
        SectorResource sectorResource = new SectorResource();
        ReflectionTestUtils.setField(sectorResource, "sectorSearchRepository", sectorSearchRepository);
        ReflectionTestUtils.setField(sectorResource, "sectorRepository", sectorRepository);
        this.restSectorMockMvc = MockMvcBuilders.standaloneSetup(sectorResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    @Before
    public void initTest() {
        sector = new Sector();
        sector.setName(DEFAULT_NAME);
        sector.setDescription(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void createSector() throws Exception {
        int databaseSizeBeforeCreate = sectorRepository.findAll().size();

        // Create the Sector

        restSectorMockMvc.perform(post("/api/sectors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sector)))
                .andExpect(status().isCreated());

        // Validate the Sector in the database
        List<Sector> sectors = sectorRepository.findAll();
        assertThat(sectors).hasSize(databaseSizeBeforeCreate + 1);
        Sector testSector = sectors.get(sectors.size() - 1);
        assertThat(testSector.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testSector.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = sectorRepository.findAll().size();
        // set the field null
        sector.setName(null);

        // Create the Sector, which fails.

        restSectorMockMvc.perform(post("/api/sectors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sector)))
                .andExpect(status().isBadRequest());

        List<Sector> sectors = sectorRepository.findAll();
        assertThat(sectors).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllSectors() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get all the sectors
        restSectorMockMvc.perform(get("/api/sectors?sort=id,desc"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.[*].id").value(hasItem(sector.getId().intValue())))
                .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
                .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())));
    }

    @Test
    @Transactional
    public void getSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

        // Get the sector
        restSectorMockMvc.perform(get("/api/sectors/{id}", sector.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(sector.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingSector() throws Exception {
        // Get the sector
        restSectorMockMvc.perform(get("/api/sectors/{id}", Long.MAX_VALUE))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

		int databaseSizeBeforeUpdate = sectorRepository.findAll().size();

        // Update the sector
        sector.setName(UPDATED_NAME);
        sector.setDescription(UPDATED_DESCRIPTION);

        restSectorMockMvc.perform(put("/api/sectors")
                .contentType(TestUtil.APPLICATION_JSON_UTF8)
                .content(TestUtil.convertObjectToJsonBytes(sector)))
                .andExpect(status().isOk());

        // Validate the Sector in the database
        List<Sector> sectors = sectorRepository.findAll();
        assertThat(sectors).hasSize(databaseSizeBeforeUpdate);
        Sector testSector = sectors.get(sectors.size() - 1);
        assertThat(testSector.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testSector.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    public void deleteSector() throws Exception {
        // Initialize the database
        sectorRepository.saveAndFlush(sector);

		int databaseSizeBeforeDelete = sectorRepository.findAll().size();

        // Get the sector
        restSectorMockMvc.perform(delete("/api/sectors/{id}", sector.getId())
                .accept(TestUtil.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());

        // Validate the database is empty
        List<Sector> sectors = sectorRepository.findAll();
        assertThat(sectors).hasSize(databaseSizeBeforeDelete - 1);
    }
}
