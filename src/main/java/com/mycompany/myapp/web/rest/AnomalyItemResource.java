package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.service.AnomalyItemService;
import com.mycompany.myapp.service.SoundWavService;
import com.mycompany.myapp.service.dto.SoundWavDTO;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import com.mycompany.myapp.service.dto.AnomalyItemDTO;
import com.mycompany.myapp.service.dto.AnomalyItemCriteria;
import com.mycompany.myapp.service.AnomalyItemQueryService;

import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.AnomalyItem}.
 */
@RestController
@RequestMapping("/api")
public class AnomalyItemResource {

    private final Logger log = LoggerFactory.getLogger(AnomalyItemResource.class);

    private static final String ENTITY_NAME = "anomalyItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnomalyItemService anomalyItemService;
    private final SoundWavService soundWavService;

    private final AnomalyItemQueryService anomalyItemQueryService;

    public AnomalyItemResource(AnomalyItemService anomalyItemService, SoundWavService soundWavService, AnomalyItemQueryService anomalyItemQueryService) {
        this.anomalyItemService = anomalyItemService;
        this.soundWavService = soundWavService;
        this.anomalyItemQueryService = anomalyItemQueryService;
    }

    /**
     * {@code POST  /anomaly-items} : Create a new anomalyItem.
     *
     * @param anomalyItemDTO the anomalyItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new anomalyItemDTO, or with status {@code 400 (Bad Request)} if the anomalyItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/anomaly-items")
    public ResponseEntity<AnomalyItemDTO> createAnomalyItem(@RequestBody AnomalyItemDTO anomalyItemDTO) throws URISyntaxException {
        log.debug("REST request to save AnomalyItem : {}", anomalyItemDTO);
        if (anomalyItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new anomalyItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        AnomalyItemDTO result = anomalyItemService.save(anomalyItemDTO);
        return ResponseEntity.created(new URI("/api/anomaly-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /anomaly-items} : Updates an existing anomalyItem.
     *
     * @param anomalyItemDTO the anomalyItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anomalyItemDTO,
     * or with status {@code 400 (Bad Request)} if the anomalyItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the anomalyItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/anomaly-items")
    public ResponseEntity<AnomalyItemDTO> updateAnomalyItem(@RequestBody AnomalyItemDTO anomalyItemDTO) throws URISyntaxException {
        log.debug("REST request to update AnomalyItem : {}", anomalyItemDTO);
        if (anomalyItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        AnomalyItemDTO result = anomalyItemService.save(anomalyItemDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, anomalyItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /anomaly-items} : get all the anomalyItems.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of anomalyItems in body.
     */
    @GetMapping("/anomaly-items")
    public ResponseEntity<List<AnomalyItemDTO>> getAllAnomalyItems(AnomalyItemCriteria criteria, Pageable pageable) {
        log.debug("REST request to get AnomalyItems by criteria: {}", criteria);
        Page<AnomalyItemDTO> page = anomalyItemQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /anomaly-items/count} : count all the anomalyItems.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/anomaly-items/count")
    public ResponseEntity<Long> countAnomalyItems(AnomalyItemCriteria criteria) {
        log.debug("REST request to count AnomalyItems by criteria: {}", criteria);
        return ResponseEntity.ok().body(anomalyItemQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /anomaly-items/:id} : get the "id" anomalyItem.
     *
     * @param id the id of the anomalyItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the anomalyItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/anomaly-items/{id}")
    public ResponseEntity<AnomalyItemDTO> getAnomalyItem(@PathVariable Long id) {
        log.debug("REST request to get AnomalyItem : {}", id);
        Optional<AnomalyItemDTO> anomalyItemDTO = anomalyItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(anomalyItemDTO);
    }

    /**
     * {@code DELETE  /anomaly-items/:id} : delete the "id" anomalyItem.
     *
     * @param id the id of the anomalyItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/anomaly-items/{id}")
    public ResponseEntity<Void> deleteAnomalyItem(@PathVariable Long id) {
        log.debug("REST request to delete AnomalyItem : {}", id);
        anomalyItemService.delete(id);
        return ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString())).build();
    }

    @GetMapping("/sound-wav-update")
    public ResponseEntity<Boolean> saveSound(){
        soundWavService.saveWaveData();
        return ResponseEntity.ok(true);
    }

    @GetMapping("/sound-wav/{id}")
    public ResponseEntity<SoundWavDTO> getSoundWav(@PathVariable String id){
        return ResponseUtil.wrapOrNotFound(  soundWavService.findOne(id));
    }
}
