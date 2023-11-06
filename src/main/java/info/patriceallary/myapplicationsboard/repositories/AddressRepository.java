package info.patriceallary.myapplicationsboard.repositories;

import info.patriceallary.myapplicationsboard.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    Address findById(long id);

}
