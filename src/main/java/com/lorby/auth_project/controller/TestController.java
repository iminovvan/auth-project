package com.lorby.auth_project.controller;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Hidden
public class TestController {

    @GetMapping("/protected")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok("You have access to a protected source!");
    }

    @GetMapping("/smtp-connection")
    public ResponseEntity<String> testSmtpConnection() {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress("smtp.office365.com", 587), 10000);
            return ResponseEntity.ok("Connection successful");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Connection failed: " + e.getMessage());
        }
    }

}
