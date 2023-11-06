package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.ContactRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRoleRepository extends JpaRepository<ContactRole, Long> {

    ContactRole findById(long id);

}
