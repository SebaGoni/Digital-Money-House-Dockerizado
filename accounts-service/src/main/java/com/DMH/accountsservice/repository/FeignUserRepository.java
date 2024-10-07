package com.DMH.accountsservice.repository;

import com.DMH.accountsservice.feignCustomExceptions.CustomErrorDecoder;
import com.DMH.accountsservice.feignCustomExceptions.FeignConfig;
import com.DMH.accountsservice.entities.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "users-service", url = "http://users-service:8081/user", configuration = {FeignConfig.class, CustomErrorDecoder.class})
public interface FeignUserRepository {

    @GetMapping("/{id}")
    User getUserById(@PathVariable Long id);

    @GetMapping("/keycloak-id/{kcId}")
    Long getUserByKeycloakId(@PathVariable String kcId);

    @GetMapping("/alias/{alias}")
    Long getUserIdByAlias(@PathVariable String alias);

    @GetMapping("/cvu/{cvu}")
    Long getUserIdByCvu(@PathVariable String cvu);

}
