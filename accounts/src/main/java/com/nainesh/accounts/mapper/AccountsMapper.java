package com.nainesh.accounts.mapper;

import com.nainesh.accounts.dto.AccountsDto;
import com.nainesh.accounts.entity.Accounts;
import org.springframework.beans.BeanUtils;

/**
 * Connects dto to entity and vice-versa
 * libraries: mode-mapper, map struct (not officially recommended)
 * BeanUtils can be used.
 *
 */
public class AccountsMapper {

    public static AccountsDto mapToAccountsDto(Accounts accounts, AccountsDto accountsDto) {
        accountsDto.setAccountNumber(accounts.getAccountNumber());
        accountsDto.setAccountType(accounts.getAccountType());
        accountsDto.setBranchAddress(accounts.getBranchAddress());
//        BeanUtils.copyProperties(accounts, accountsDto);
        return accountsDto;
    }

    public static Accounts mapToAccounts(AccountsDto accountsDto, Accounts accounts) {
        accounts.setAccountNumber(accountsDto.getAccountNumber());
        accounts.setAccountType(accountsDto.getAccountType());
        accounts.setBranchAddress(accountsDto.getBranchAddress());
        return accounts;
    }
}
