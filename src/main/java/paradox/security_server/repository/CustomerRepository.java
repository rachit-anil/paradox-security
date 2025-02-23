package paradox.security_server.repository;

import org.springframework.stereotype.Repository;
import paradox.security_server.models.Customer;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer,Long> {

    Optional<Customer> findByEmail(String email);

}
