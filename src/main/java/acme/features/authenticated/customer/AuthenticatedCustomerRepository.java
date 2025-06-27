
package acme.features.authenticated.customer;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.components.principals.UserAccount;
import acme.client.repositories.AbstractRepository;
import acme.realms.Customer;

@Repository
public interface AuthenticatedCustomerRepository extends AbstractRepository {

	@Query("select user from UserAccount user where user.id = :userAccountId")
	UserAccount findUserAccountById(int userAccountId);

	@Query("select c from Customer c where c.userAccount.id = :userAccountId")
	Customer findCustomerByUserAccountId(int userAccountId);
}
