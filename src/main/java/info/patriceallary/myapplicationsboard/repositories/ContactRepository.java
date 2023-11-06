package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.Contact;
import info.patriceallary.myapplicationsboard.domain.Enterprise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    Contact findById(long id);

    List<Contact> findContactsByEnterprise(Enterprise enterprise);

}
