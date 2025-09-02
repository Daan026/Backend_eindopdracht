package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.entity.UserProfile;
import com.fondsdelecturelibre.repository.UserProfileRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private UserRepository userRepository;

    @PostMapping("/photo")
    public ResponseEntity<String> uploadProfilePhoto(@RequestParam("file") MultipartFile file) {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Gebruiker niet gevonden");
            }
            User user = userOpt.get();
            
            if (file.getSize() > 1048576) {
                return ResponseEntity.badRequest().body("Bestand te groot. Maximaal 1MB toegestaan.");
            }
            
            // Find or create user profile
            UserProfile profile = userProfileRepository.findByUserId(user.getId());
            if (profile == null) {
                profile = new UserProfile();
                profile.setUser(user);
            }
            
            profile.setProfilePhoto(file.getBytes());
            userProfileRepository.save(profile);
            
            return ResponseEntity.ok("Profielfoto succesvol geüpload voor gebruiker: " + username);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout bij uploaden: " + e.getMessage());
        }
    }

    @GetMapping("/photo")
    public ResponseEntity<byte[]> getProfilePhoto() {
        try {
            // Get current authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            
            Optional<User> userOpt = userRepository.findByUsername(username);
            if (userOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            User user = userOpt.get();
            
            UserProfile profile = userProfileRepository.findByUserId(user.getId());
            if (profile != null && profile.getProfilePhoto() != null) {
                // Bepaal filename in Fonds de lecture libre formaat
                String filename = "Fonds_de_lecture_libre_user_profile_picture.jpeg";
                
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header("Content-Disposition", "attachment; filename=\"" + filename + "\"")
                    .body(profile.getProfilePhoto());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
