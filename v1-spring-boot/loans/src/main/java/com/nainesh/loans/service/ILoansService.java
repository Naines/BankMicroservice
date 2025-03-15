package com.nainesh.loans.service;

import com.nainesh.loans.dto.LoansDto;

public interface ILoansService {
    void createLoan(String mobileNumber);

    LoansDto fetchLoan(String mobileNumber);

    boolean updateLoan(LoansDto loansDto);

    boolean deleteLoan(String mobileNumber);
}
