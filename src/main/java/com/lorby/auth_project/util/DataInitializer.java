package com.lorby.auth_project.util;

import com.lorby.auth_project.entity.Role;
import com.lorby.auth_project.repository.RoleRepository;
import com.lorby.auth_project.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer {
    private final RoleRepository roleRepository;

    @PostConstruct
    public void seed() {
        if (!roleRepository.findByName("USER").isPresent()) {
            Role userRole = new Role("USER");
            roleRepository.save(userRole);
        }

        if (!roleRepository.findByName("ADMIN").isPresent()) {
            Role adminRole = new Role("ADMIN");
            roleRepository.save(adminRole);
        }
    }

}
