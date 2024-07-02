package com.lorby.auth_project.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Hidden
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("You have access to a protected source!");
    }

}
