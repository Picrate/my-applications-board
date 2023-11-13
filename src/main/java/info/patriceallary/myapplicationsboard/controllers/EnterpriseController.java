package info.patriceallary.myapplicationsboard.controllers;

import info.patriceallary.myapplicationsboard.domain.Enterprise;
import info.patriceallary.myapplicationsboard.domain.EnterpriseActivity;
import info.patriceallary.myapplicationsboard.domain.EnterpriseType;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseActivityRepository;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseRepository;
import info.patriceallary.myapplicationsboard.repositories.EnterpriseTypeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

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
     *
     * @param requestedId
     * @return HTTP Response OK (200) & the requested Type in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @GetMapping("/types/{requestedId}")
    public ResponseEntity<EnterpriseType> getEnterpriseType(@PathVariable Long requestedId) {
        if (typeRepository.existsById(requestedId)) {
            return ResponseEntity.ok(this.typeRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP GET All EnterpriseTypes
     *
     * @return HTTP Response OK (200) & the TypeList in Response Body
     */
    @GetMapping("/types")
    public ResponseEntity<List<EnterpriseType>> getAllEnterpriseTypes() {
        return ResponseEntity.ok(this.typeRepository.findAll());
    }

    /**
     * HTTP PUT EnterpriseType
     *
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) & the requested Type in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @PutMapping("/types/{requestedId}")
    public ResponseEntity<Void> updateEnterpriseType(@PathVariable Long requestedId, @RequestBody EnterpriseType updatedType) {
        if (this.typeRepository.existsById(requestedId)) {
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
     *
     * @return HTTP Response OK (200) & the location of new EnterpriseType in Headers
     */
    @PostMapping("/types")
    public ResponseEntity<Void> createNewEnterpriseType(@RequestBody EnterpriseType newType, UriComponentsBuilder ucb) {
        this.typeRepository.save(newType);
        URI location = ucb
                .path("/enterprises/types/{id}")
                .buildAndExpand(newType.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * HTTP DELETE EnterpriseType
     *
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) if exists
     * else HTTP NOT_FOUND (404);
     */
    @DeleteMapping("/types/{requestedId}")
    public ResponseEntity<Void> deleteEnterpriseType(@PathVariable Long requestedId) {
        if (this.typeRepository.existsById(requestedId)) {

            this.typeRepository.delete(this.typeRepository.findById(requestedId).get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ENTERPRISE ACTIVITY API
     */

    /**
     * HTTP GET EnterpriseActivity from Id
     *
     * @param requestedId
     * @return HTTP Response OK (200) & the requested Type in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @GetMapping("/activities/{requestedId}")
    public ResponseEntity<EnterpriseActivity> getEnterpriseActivity(@PathVariable Long requestedId) {
        if (this.activityRepository.existsById(requestedId)) {
            return ResponseEntity.ok(this.activityRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP GET EnterpriseActivity List
     *
     * @return HTTP Response OK (200) & the Activity List in Response Body
     */
    @GetMapping("/activities")
    public ResponseEntity<List<EnterpriseActivity>> getAllActivites() {
        return ResponseEntity.ok(this.activityRepository.findAll());
    }


    /**
     * HTTP POST EnterpriseActivity
     *
     * @return HTTP Response OK (200) & the location of new EnterpriseType in Headers
     */
    @PostMapping("/activities")
    public ResponseEntity<Void> createANewActivity(@RequestBody EnterpriseActivity newActivity, UriComponentsBuilder ucb) {
        this.activityRepository.save(newActivity);
        URI location = ucb
                .path("/enterprises/activities/{id}")
                .buildAndExpand(newActivity.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    /**
     * HTTP PUT EnterpriseActivity
     *
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) & the requested Activity in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @PutMapping("/activities/{requestedId}")
    public ResponseEntity<Void> updateEnterpriseActivity(@PathVariable Long requestedId, @RequestBody EnterpriseActivity updatedActivity) {
        if (this.activityRepository.existsById(requestedId)) {
            EnterpriseActivity activityToUpdate = this.activityRepository.findById(requestedId).get();
            activityToUpdate.setActivity(updatedActivity.getActivity());
            this.activityRepository.save(activityToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP DELETE EnterpriseActivity
     *
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) if exists
     * else HTTP NOT_FOUND (404);
     * Throws an ViolationConstraint Exception if Activity is referenced
     */
    @DeleteMapping("/activities/{requestedId}")
    public ResponseEntity<Void> deleteEnterpriseActivity(@PathVariable Long requestedId) {
        if (this.activityRepository.existsById(requestedId)) {
            this.activityRepository.delete(this.activityRepository.findById(requestedId).get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * ENTERPRISE API
     */
    @GetMapping("{requestedId}")
    public ResponseEntity<Enterprise> getEnterprise(@PathVariable Long requestedId) {
        if (this.enterpriseRepository.existsById(requestedId)) {
            return ResponseEntity.ok(this.enterpriseRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping()
    public ResponseEntity<List<Enterprise>> getEnterpriseList(Pageable pageable) {
        Page<Enterprise> page = this.enterpriseRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "name"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @PostMapping()
    public ResponseEntity<Void> createNewEnterprise(@RequestBody Enterprise newEnterprise, UriComponentsBuilder ucb) {
        if (newEnterprise != null) {
            this.enterpriseRepository.save(newEnterprise);
            URI location = ucb
                    .path("/enterprises/{id}")
                    .buildAndExpand(newEnterprise.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } else {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateAnExistingEnterprise(@PathVariable Long requestedId, @RequestBody Enterprise updatedEnterprise) {
        if (this.enterpriseRepository.existsById(requestedId)) {
           if (updatedEnterprise != null) {
               Enterprise enterpriseToUpdate = this.enterpriseRepository.findById(requestedId).get();
               enterpriseToUpdate.setName(updatedEnterprise.getName());
               enterpriseToUpdate.setPhone(updatedEnterprise.getPhone());
               enterpriseToUpdate.setNotes(updatedEnterprise.getNotes());
               enterpriseToUpdate.setAddress(updatedEnterprise.getAddress());
               enterpriseToUpdate.setActivity(updatedEnterprise.getActivity());
               enterpriseToUpdate.setType(updatedEnterprise.getType());
               this.enterpriseRepository.save(enterpriseToUpdate);
               return ResponseEntity.noContent().build();
           } else {
               return ResponseEntity.unprocessableEntity().build();
           }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable Long requestedId) {
        if(this.enterpriseRepository.existsById(requestedId)) {
            this.enterpriseRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }




}
