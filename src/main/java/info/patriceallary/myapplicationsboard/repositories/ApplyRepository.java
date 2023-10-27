package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.Apply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ApplyRepository extends JpaRepository<Apply, Long> {

    Apply findById(long id);

    List<Apply> findApplyByDateCreated(LocalDateTime date);

}
