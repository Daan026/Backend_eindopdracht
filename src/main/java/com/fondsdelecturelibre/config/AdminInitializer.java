package com.fondsdelecturelibre.config;

import com.fondsdelecturelibre.entity.Role;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.entity.UserProfile;
import com.fondsdelecturelibre.repository.RoleRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import com.fondsdelecturelibre.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Set;

@Component
@Order(1)
public class AdminInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileRepository userProfileRepository;

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
            User savedAdmin = userRepository.save(admin);
            
            // Automatisch Admin profielfoto laden uit resources
            createAdminProfileWithPhoto(savedAdmin);
            
            System.out.println("Standaard admin-gebruiker aangemaakt: gebruikersnaam 'Admin', wachtwoord 'fondsdelecturelibre'");
            System.out.println("Admin profielfoto automatisch geladen uit resources/Admin.png");
        }
    }
    
    private void createAdminProfileWithPhoto(User admin) {
        try {
            // Controleer of er al een UserProfile bestaat voor deze admin
            UserProfile existingProfile = userProfileRepository.findByUserId(admin.getId());
            if (existingProfile == null) {
                // Laad Admin.png uit resources
                ClassPathResource resource = new ClassPathResource("Admin.png");
                byte[] photoBytes = resource.getInputStream().readAllBytes();
                
                // Maak nieuwe UserProfile aan met profielfoto
                UserProfile adminProfile = new UserProfile();
                adminProfile.setUser(admin);
                adminProfile.setProfilePhoto(photoBytes);
                adminProfile.setFirstName("Admin");
                adminProfile.setLastName("User");
                
                userProfileRepository.save(adminProfile);
                System.out.println("Admin profielfoto succesvol geladen: " + photoBytes.length + " bytes");
            } else {
                System.out.println("Admin UserProfile bestaat al, profielfoto niet overschreven");
            }
        } catch (IOException e) {
            System.err.println("Fout bij laden van Admin profielfoto: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
