package com.nainesh.accounts.repository;


import com.nainesh.accounts.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    //Derived name method in spring JPA
    Optional<Customer> findByMobileNumber(String mobileNumber);

}
