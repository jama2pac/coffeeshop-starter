package ek.osnb.demo.coffeeshop.customer;

import ek.osnb.demo.coffeeshop.common.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
class CustomerService implements CustomerApi {

    private final CustomerRepository repository;

    CustomerService(CustomerRepository repository) {
        this.repository = repository;
    }

    // ---------- Used by CustomerController ----------

    // 404 until the customer has saved their profile for the first time
    @Transactional(readOnly = true)
    CustomerResponse findMine(String email) {
        return repository.findByEmail(Customer.normalizeEmail(email))
                .map(CustomerMapper::toResponse)
                .orElseThrow(() -> new NotFoundException("There is no customer with email " + email));
    }

    CustomerResponse saveMine(String email, CustomerRequest request) {
        Optional<Customer> existing = repository.findByEmail(Customer.normalizeEmail(email));

        // No save needed: Hibernate writes changes to a loaded entity when the transaction ends.
        if (existing.isPresent()) {
            Customer customer = existing.get();
            customer.updateDetails(request.name(), email, request.street(), request.postalCode(), request.city());
            return CustomerMapper.toResponse(customer);
        }

        Customer customer = Customer.create(request.name(), email, request.street(), request.postalCode(), request.city());
        return CustomerMapper.toResponse(repository.save(customer));
    }


    // ---------- CustomerApi: used by other features ----------

    @Override
    @Transactional(readOnly = true)
    public CustomerInfo getCustomer(Long customerId) {
        return CustomerMapper.toInfo(findCustomer(customerId));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerInfo> findCustomerByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }

        return repository.findByEmail(Customer.normalizeEmail(email))
                .map(CustomerMapper::toInfo);
    }

    private Customer findCustomer(Long id) {
        Optional<Customer> result = repository.findById(id);

        if (result.isEmpty()) {
            throw new NotFoundException("Customer not found: " + id);
        }

        return result.get();
    }
}
