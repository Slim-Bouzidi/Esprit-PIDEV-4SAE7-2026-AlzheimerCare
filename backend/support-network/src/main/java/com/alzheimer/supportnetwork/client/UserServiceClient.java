package com.alzheimer.supportnetwork.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserServiceClient {

    @GetMapping("/api/users")
    List<UserServiceUserDto> getAllUsers();
}

