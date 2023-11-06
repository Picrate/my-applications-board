package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.ContactTitle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactTitleRepository extends JpaRepository<ContactTitle, Long> {

    ContactTitle findById(long id);

}
