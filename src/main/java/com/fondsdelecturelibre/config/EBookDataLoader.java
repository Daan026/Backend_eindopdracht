package com.fondsdelecturelibre.config;

import com.fondsdelecturelibre.entity.EBook;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.repository.EBookRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@Order(2)
public class EBookDataLoader implements CommandLineRunner {

    @Autowired
    private EBookRepository ebookRepository;
    
    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        loadSampleBooks();
    }

    private void loadSampleBooks() {
        // Zoek de admin gebruiker om als uploader te gebruiken
        Optional<User> adminUser = userRepository.findByUsername("Admin");
        if (adminUser.isEmpty()) {
            System.out.println("Admin gebruiker niet gevonden. Sample boeken worden niet geladen.");
            return;
        }

        User admin = adminUser.get();

        // Definieer de boeken met metadata
        String[][] bookData = {
            {"Alles wat je dragen kan.epub", "Alles wat je dragen kan", "Onbekende Auteur", "Een verhaal over kracht en doorzettingsvermogen."},
            {"Wacht op mij.epub", "Wacht op mij", "Onbekende Auteur", "Een ontroerend verhaal over wachten en hoop."},
            {"Wat stilte wil.epub", "Wat stilte wil", "Onbekende Auteur", "Een poëtisch verhaal over de kracht van stilte."},
            {"Zomerzussen.epub", "Zomerzussen", "Onbekende Auteur", "Een hartverwarmend verhaal over zusterschap en zomerse avonturen."}
        };

        for (String[] book : bookData) {
            String fileName = book[0];
            String title = book[1];
            String author = book[2];
            String description = book[3];

            // Controleer of het boek al bestaat
            if (ebookRepository.findByFileName(fileName).isEmpty()) {
                try {
                    loadBook(fileName, title, author, description, admin);
                    System.out.println("Sample boek geladen: " + title);
                } catch (Exception e) {
                    System.err.println("Fout bij laden van boek " + fileName + ": " + e.getMessage());
                }
            } else {
                System.out.println("Boek " + title + " bestaat al in de database.");
            }
        }
    }

    private void loadBook(String fileName, String title, String author, String description, User user) throws IOException {
        // Laad het bestand uit de resources via InputStream (werkt overal)
        ClassPathResource resource = new ClassPathResource("Ebooks/" + fileName);
        
        if (!resource.exists()) {
            throw new IOException("Bestand niet gevonden: " + fileName);
        }

        byte[] fileContent;
        try (InputStream inputStream = resource.getInputStream()) {
            fileContent = inputStream.readAllBytes();
        }

        // Maak een nieuw EBook object
        EBook ebook = new EBook();
        ebook.setTitle(title);
        ebook.setAuthor(author);
        ebook.setDescription(description);
        ebook.setFileName(fileName);
        ebook.setFileType("application/epub+zip");
        ebook.setFileSize((long) fileContent.length);
        ebook.setUploadDate(LocalDateTime.now());
        ebook.setFileContent(fileContent);
        ebook.setUser(user);

        // Sla het boek op in de database
        ebookRepository.save(ebook);
    }
}
