package info.patriceallary.myapplicationsboard.controllers;

import info.patriceallary.myapplicationsboard.domain.EnterpriseType;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseActivityRepository;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseRepository;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseTypeRepository;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.net.URI;
@RestController
@RequestMapping("/enterprises")
public class EnterpriseController {

    private final EnterpriseRepository enterpriseRepository;
    private final EnterpriseTypeRepository typeRepository;
    private final EnterpriseActivityRepository activityRepository;

    public EnterpriseController(EnterpriseRepository enterpriseRepository, EnterpriseTypeRepository typeRepository, EnterpriseActivityRepository activityRepository) {
        this.enterpriseRepository = enterpriseRepository;
        this.typeRepository = typeRepository;
        this.activityRepository = activityRepository;
    }

    /**
     * ENTERPRISE TYPE API
     */

    /**
     * HTTP GET EnterpriseType from Id
     * @param requestedId
     * @return HTTP Response OK (200) & the requested Type in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @GetMapping("/types/{requestedId}")
    public ResponseEntity<EnterpriseType> getEnterpriseType(@PathVariable Long requestedId){
        if(typeRepository.existsById(requestedId)){
            return ResponseEntity.ok(this.typeRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP GET All EnterpriseTypes
     * @return HTTP Response OK (200) & the TypeList in Response Body
     */
    @GetMapping("/types")
    public ResponseEntity<List<EnterpriseType>> getAllEnterpriseTypes(){
        return ResponseEntity.ok(this.typeRepository.findAll());
    }

    /**
     * HTTP PUT EnterpriseType
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) & the requested Type in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @PutMapping("/types/{requestedId}")
    public ResponseEntity<Void> updateEnterpriseType(@PathVariable Long requestedId, @RequestBody EnterpriseType updatedType){
        if(this.typeRepository.existsById(requestedId)){
            EnterpriseType typeToUpdate = this.typeRepository.findById(requestedId).get();
            typeToUpdate.setType(updatedType.getType());
            this.typeRepository.save(typeToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP POST EnterpriseType
     * @return HTTP Response OK (200) & the location of new EnterpriseType in Headers
     */
    @PostMapping("/types")
    public ResponseEntity<Void> createNewEnterpriseType(@RequestBody EnterpriseType newType, UriComponentsBuilder ucb){
        this.typeRepository.save(newType);
        URI location = ucb
                .path("/enterprises/types/{id}")
                .buildAndExpand(newType.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * HTTP DELETE EnterpriseType
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) if exists
     * else HTTP NOT_FOUND (404);
     */
    @DeleteMapping("/types/{requestedId}")
    public ResponseEntity<Void> deleteEnterpriseType(@PathVariable Long requestedId){
        if (this.typeRepository.existsById(requestedId)) {

                this.typeRepository.delete(this.typeRepository.findById(requestedId).get());
                return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }








}
