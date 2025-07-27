package com.fondsdelecturelibre.config;

import com.fondsdelecturelibre.entity.Role;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.repository.RoleRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.Set;

@Component
public class AdminInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        String adminUsername = "Admin";
        String adminEmail = "admin@fondsdelecturelibre.com";
        String adminPassword = "fondsdelecturelibre";
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Role adminRole = roleRepository.findByName(Role.ERole.ROLE_ADMIN)
                .orElseGet(() -> roleRepository.save(new Role(Role.ERole.ROLE_ADMIN)));
            
            Role memberRole = roleRepository.findByName(Role.ERole.ROLE_MEMBER)
                .orElseGet(() -> roleRepository.save(new Role(Role.ERole.ROLE_MEMBER)));
            
            User admin = new User(adminUsername, passwordEncoder.encode(adminPassword), adminEmail);
            admin.setRoles(Set.of(adminRole, memberRole));
            userRepository.save(admin);
            System.out.println("Standaard admin-gebruiker aangemaakt: gebruikersnaam 'Admin', wachtwoord 'fondsdelecturelibre'");
        }
    }
}
