package info.patriceallary.myapplicationsboard.controllers;


import info.patriceallary.myapplicationsboard.domain.Contact;
import info.patriceallary.myapplicationsboard.domain.ContactRole;
import info.patriceallary.myapplicationsboard.domain.ContactTitle;
import info.patriceallary.myapplicationsboard.repositories.ContactRepository;
import info.patriceallary.myapplicationsboard.repositories.ContactRoleRepository;
import info.patriceallary.myapplicationsboard.repositories.ContactTitleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    private final ContactRoleRepository roleRepository;
    private final ContactTitleRepository titleRepository;
    private final ContactRepository contactRepository;

    public ContactController(ContactRoleRepository roleRepository, ContactTitleRepository titleRepository, ContactRepository contactRepository) {
        this.roleRepository = roleRepository;
        this.titleRepository = titleRepository;
        this.contactRepository = contactRepository;
    }

    /**
     * CONTACT ROLES API
     */

    /**
     * HTTP GET ContactRole from Id
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
     * HTTP GET list of roles
     * @param pageable Page Number, Size & Sort Parameters
     * @return List of ContactRole according to Page URI Parameters
     */
    @GetMapping("/roles")
    public ResponseEntity<List<ContactRole>> getAllRoles(Pageable pageable){
        Page<ContactRole> page = this.roleRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "role"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    /**
     * HTTP PUT ContactRole
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) & the requested Role in Response Body if exists
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

    /**
     * HTTP POST ContactRole
     * @param newRole
     * @param ucb
     * @return HTTP Response CREATED (201) | HEADER : new ContactRole URI
     */
    @PostMapping("/roles")
    public ResponseEntity<Void> createRole(@RequestBody ContactRole newRole, UriComponentsBuilder ucb) {
        this.roleRepository.save(newRole);
        URI newRoleLocation = ucb
                .path("/contacts/roles/{id}")
                .buildAndExpand(newRole.getId())
                .toUri();
        return ResponseEntity.created(newRoleLocation).build();
    }

    /**
     * HTTP DELETE ContactRole
     * @param requestedId
     * @return HTTP Response NO_CONTENT (204) if ContactRole exists else NOT_FOUND (404)
     */
    @DeleteMapping("/roles/{requestedId}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long requestedId){
        if(this.roleRepository.findById(requestedId).isPresent()){
            this.roleRepository.delete(this.roleRepository.findById(requestedId).get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * CONTACT TITLES API
     */
    @GetMapping("/titles/{requestedId}")
    public ResponseEntity<ContactTitle> getTitle(@PathVariable Long requestedId){
        if(this.titleRepository.findById(requestedId).isPresent()){
            return ResponseEntity.ok(this.titleRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/titles/{requestedId}")
    public ResponseEntity<Void> updateTitle(@PathVariable Long requestedId, @RequestBody ContactTitle updatedTitle){
        if(this.titleRepository.findById(requestedId).isPresent()){
            ContactTitle titleToUpdate = this.titleRepository.findById(requestedId).get();
            titleToUpdate.setTitle(updatedTitle.getTitle());
            this.titleRepository.save(titleToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/titles")
    public ResponseEntity<Void> saveNewTitle(@RequestBody ContactTitle newTitle, UriComponentsBuilder ucb){
        if(newTitle.getTitle().isEmpty()){
            return ResponseEntity.badRequest().build();
        } else {
            this.titleRepository.save(newTitle);
            URI locationOfNewTitle = ucb
                    .path("/contacts/titles/{id}")
                    .buildAndExpand(newTitle.getId())
                    .toUri();
            return ResponseEntity.created(locationOfNewTitle).build();
        }
    }

    @DeleteMapping("/titles/{requestedId}")
    public ResponseEntity<Void> deleteTitle(@PathVariable Long requestedId){
        if(this.titleRepository.findById(requestedId).isPresent()){
            this.titleRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/titles")
    public ResponseEntity<List<ContactTitle>> getAllTitles(){
        return ResponseEntity.ok(this.titleRepository.findAll());
    }

    /**
     * Contact API
     */
    @GetMapping("/{requestedId}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long requestedId) {
        if(this.contactRepository.existsById(requestedId)){
            return ResponseEntity.ok(this.contactRepository.findById(requestedId).get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateContact(@PathVariable Long requestedId, @RequestBody Contact updatedContact){
        if (this.contactRepository.existsById(requestedId)) {
            this.contactRepository.save(updatedContact);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping()
    public ResponseEntity<Void> saveNewContact(@RequestBody Contact newContact, UriComponentsBuilder ucb){
        if(newContact != null) {
            this.contactRepository.save(newContact);
            URI location = ucb
                    .path("/contacts/{id}")
                    .buildAndExpand(newContact.getId())
                    .toUri();
            return ResponseEntity.created(location).build();
        } else
        {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("{requestedId}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long requestedId){
        if(this.contactRepository.existsById(requestedId)){
            this.contactRepository.delete(this.contactRepository.findById(requestedId).get());
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }







}
