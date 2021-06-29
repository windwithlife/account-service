package com.simple.example.client;

import com.simple.example.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import com.simple.example.AccountConstant;
import com.simple.common.api.BaseResponse;
import com.simple.common.auth.AuthConstant;

import javax.validation.Valid;

@FeignClient(name = AccountConstant.SERVICE_NAME, path = "/simple/account", url = "${simple.example-service-endpoint}")
// TODO Client side validation can be enabled as needed
// @Validated
public interface ExampleClient {

    @PostMapping(path = "/create")
    GenericAccountResponse createAccount(@RequestHeader(AuthConstant.AUTHORIZATION_HEADER) String authz, @RequestBody @Valid CreateAccountRequest request);

}
