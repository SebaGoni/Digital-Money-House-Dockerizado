package com.dh.DMH.usersservice.service.client;

import com.dh.DMH.usersservice.dto.AccountCreationRequest;
import com.dh.DMH.usersservice.dto.AccountResponse;
import com.dh.DMH.usersservice.dto.UserAliasUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "accounts-service", url = "http://accounts-service:8085")
public interface AccountClient {

    @PostMapping("/accounts/create")
    AccountResponse createAccount(AccountCreationRequest request);
}
