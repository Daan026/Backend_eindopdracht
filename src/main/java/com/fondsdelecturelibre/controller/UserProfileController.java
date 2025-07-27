package com.fondsdelecturelibre.controller;

import com.fondsdelecturelibre.entity.UserProfile;
import com.fondsdelecturelibre.repository.UserProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/userprofile")
public class UserProfileController {

    @Autowired
    private UserProfileRepository userProfileRepository;

    @PostMapping("/{id}/photo")
    public ResponseEntity<String> uploadProfilePhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 1048576) {
                return ResponseEntity.badRequest().body("Bestand te groot. Maximaal 1MB toegestaan.");
            }
            
            UserProfile profile = userProfileRepository.findById(id).orElse(new UserProfile());
            profile.setProfilePhoto(file.getBytes());
            userProfileRepository.save(profile);
            
            return ResponseEntity.ok("Profielfoto succesvol geüpload");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Fout bij uploaden: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getProfilePhoto(@PathVariable Long id) {
        try {
            UserProfile profile = userProfileRepository.findById(id).orElse(null);
            if (profile != null && profile.getProfilePhoto() != null) {
                return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(profile.getProfilePhoto());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
