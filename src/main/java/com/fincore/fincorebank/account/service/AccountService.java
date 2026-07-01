package com.fincore.fincorebank.account.service;


import java.util.List;

import com.fincore.fincorebank.account.dtos.AccountDTO;
import com.fincore.fincorebank.account.entity.Account;
import com.fincore.fincorebank.auth_users.entity.User;
import com.fincore.fincorebank.enums.AccountType;
import com.fincore.fincorebank.response.Response;

public interface AccountService {
	Account createAccount(AccountType accountType, User user);
	Response<List<AccountDTO>>getMyAccounts();
	Response<?>closedAccount(String accountNumber);
}