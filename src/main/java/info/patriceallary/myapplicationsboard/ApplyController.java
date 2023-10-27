package info.patriceallary.myapplicationsboard;

import info.patriceallary.myapplicationsboard.domain.Apply;
import info.patriceallary.myapplicationsboard.repositories.ApplyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequestMapping("/applies")
public class ApplyController {

    DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    private ApplyRepository applyRepository;

    public ApplyController(ApplyRepository applyRepository) {
        this.applyRepository = applyRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<Apply> findById(@PathVariable Long requestedId) {

        if(this.applyRepository.findById(requestedId).isPresent())
        {
            return ResponseEntity.ok(this.applyRepository.findById(requestedId).get());
        }
        return ResponseEntity.notFound().build();

    }

    @PostMapping()
    public ResponseEntity<Apply> createANewApply(@RequestBody Apply newApplyRequest, UriComponentsBuilder ucb) {

        Apply savedApply = new Apply( newApplyRequest.getName(), newApplyRequest.getDateCreated());
        this.applyRepository.save(savedApply);
        URI locationOfNewApply = ucb
                .path("/applies/{id}")
                .buildAndExpand(savedApply.getId())
                .toUri();
        return ResponseEntity.created(locationOfNewApply).build();
    }

    @GetMapping()
    public ResponseEntity<List<Apply>> getAllApplies(Pageable pageable) {
        Page<Apply> page = this.applyRepository.findAll(
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "dateCreated"))
                )
        );
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Apply> updateApply(@PathVariable Long requestedId, @RequestBody Apply updatedApply) {

        if(this.applyRepository.findById(requestedId).isPresent()) {
            Apply applyToUpdate = this.applyRepository.findById(requestedId).get();
            applyToUpdate.setName(updatedApply.getName());
            applyToUpdate.setDateCreated(updatedApply.getDateCreated());
            this.applyRepository.save(applyToUpdate);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{requestedId}")
    private ResponseEntity<Void> deleteApply(@PathVariable Long requestedId) {
        if (this.applyRepository.findById(requestedId).isPresent()) {
            this.applyRepository.deleteById(requestedId);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
