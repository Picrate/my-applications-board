package info.patriceallary.myapplicationsboard.controllers;

import info.patriceallary.myapplicationsboard.domain.Address;
import info.patriceallary.myapplicationsboard.repositories.AddressRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/address")
public class AddressController {

    private final AddressRepository addressRepository;

    public AddressController(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Address> getById(@PathVariable Long requestedId) {
        if (this.addressRepository.findById(requestedId).isPresent()) {
            return ResponseEntity.ok(addressRepository.findById(requestedId).get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Void> createANewAddress(@RequestBody Address newAddressRequest, UriComponentsBuilder ucb) {

        this.addressRepository.save(newAddressRequest);
        URI location = ucb
                .path("/address/{id}")
                .buildAndExpand(newAddressRequest.getId())
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> updateAddress(@PathVariable Long requestedId, @RequestBody Address updatedAddress){

        if (this.addressRepository.findById(requestedId).isPresent()) {
            Address addressToUpdate = this.addressRepository.findById(requestedId).get();
            addressToUpdate.setAddress1(updatedAddress.getAddress1());
            addressToUpdate.setAddress2(updatedAddress.getAddress2());
            addressToUpdate.setZipCode(updatedAddress.getZipCode());
            addressToUpdate.setCity(updatedAddress.getCity());
            this.addressRepository.save(addressToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long requestedId){
        if(this.addressRepository.findById(requestedId).isPresent()){
            this.addressRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }

    }


}
