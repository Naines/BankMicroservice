package com.nainesh.accounts.service.impl;

import com.nainesh.accounts.dto.AccountsDto;
import com.nainesh.accounts.dto.CardsDto;
import com.nainesh.accounts.dto.CustomerDetailsDto;
import com.nainesh.accounts.dto.LoansDto;
import com.nainesh.accounts.entity.Accounts;
import com.nainesh.accounts.entity.Customer;
import com.nainesh.accounts.exception.ResourceNotFoundException;
import com.nainesh.accounts.mapper.AccountsMapper;
import com.nainesh.accounts.mapper.CustomerMapper;
import com.nainesh.accounts.repository.AccountsRepository;
import com.nainesh.accounts.repository.CustomerRepository;
import com.nainesh.accounts.service.ICustomerService;
import com.nainesh.accounts.service.client.CardsFeignClient;
import com.nainesh.accounts.service.client.LoansFeignClient;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements ICustomerService {

    private AccountsRepository accountsRepository;
    private CustomerRepository customerRepository;
    private CardsFeignClient cardsFeignClient;
    private LoansFeignClient loansFeignClient;

    @Override
    public CustomerDetailsDto fetchCustomerDetails(String mobileNumber, String correlationId) {
        Customer customer = customerRepository.findByMobileNumber(mobileNumber).orElseThrow(
                () -> new ResourceNotFoundException("Customer", "mobileNumber", mobileNumber));
        Accounts accounts = accountsRepository.findByCustomerId(customer.getCustomerId()).orElseThrow(
                () -> new ResourceNotFoundException("Account", "customerId", customer.getCustomerId().toString()));

        CustomerDetailsDto customerDetailsDto = CustomerMapper.mapToCustomerDetailsDto(customer, new CustomerDetailsDto());
        customerDetailsDto.setAccountsDto(AccountsMapper.mapToAccountsDto(accounts, new AccountsDto()));

        ResponseEntity<LoansDto> loansDtoResponseEntity = loansFeignClient.fetchLoansDetails(correlationId, mobileNumber);
        if(null!=loansDtoResponseEntity){
            customerDetailsDto.setLoansDto(loansDtoResponseEntity.getBody());
        }

        ResponseEntity<CardsDto> cardsDtoResponseEntity = cardsFeignClient.fetchCardsDetails(correlationId, mobileNumber);
        if(null!=cardsDtoResponseEntity){
            customerDetailsDto.setCardsDto(cardsDtoResponseEntity.getBody());
        }
        return customerDetailsDto;
    }
}
