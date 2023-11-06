package info.patriceallary.myapplicationsboard.controllers;


import info.patriceallary.myapplicationsboard.domain.ContactRole;
import info.patriceallary.myapplicationsboard.repositories.ContactRepository;
import info.patriceallary.myapplicationsboard.repositories.ContactRoleRepository;
import info.patriceallary.myapplicationsboard.repositories.ContactTitleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private ContactRoleRepository roleRepository;
    private ContactTitleRepository titleRepository;
    private ContactRepository contactRepository;

    public ContactController(ContactRoleRepository roleRepository, ContactTitleRepository titleRepository, ContactRepository contactRepository) {
        this.roleRepository = roleRepository;
        this.titleRepository = titleRepository;
        this.contactRepository = contactRepository;
    }

    /**
     * HTTP GET ContactRole
     * @param requestedId
     * @return HTTP Response OK (200) & the requested Role in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @GetMapping("/roles/{requestedId}")
    public ResponseEntity<ContactRole> getRole(@PathVariable Long requestedId){
        if(this.roleRepository.findById(requestedId).isPresent()) {
            return ResponseEntity.ok(this.roleRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP PUT ContactRole
     * @param requestedId
     * @return HTTP Response OK (200) & the requested Role in Response Body if exists
     * else HTTP NOT_FOUND (404);
     */
    @PutMapping("/roles/{requestedId}")
    public ResponseEntity<ContactRole> updateRole(@PathVariable Long requestedId, @RequestBody ContactRole newRole){

        if(this.roleRepository.findById(requestedId).isPresent()) {
            ContactRole roleToUpdate = this.roleRepository.findById(requestedId).get();
            roleToUpdate.setRole(newRole.getRole());
            this.roleRepository.save(roleToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/roles")
    public ResponseEntity<Void> createRole(@RequestBody ContactRole newRole, UriComponentsBuilder ucb) {
        this.roleRepository.save(newRole);
        URI newRoleLocation = ucb
                .path("/contacts/roles/{id}")
                .buildAndExpand(newRole.getId())
                .toUri();
        return ResponseEntity.created(newRoleLocation).build();
    }






}
