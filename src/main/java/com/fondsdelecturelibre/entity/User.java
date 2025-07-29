package com.fondsdelecturelibre.entity;

import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

@Entity
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(columnNames = "username"),
    @UniqueConstraint(columnNames = "email")
})
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class User {

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private UserProfile userProfile;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    @NotBlank(message = "Gebruikersnaam is verplicht")
    @Size(min = 3, max = 50, message = "Gebruikersnaam moet tussen 3 en 50 karakters zijn")
    private String username;

    @Column(nullable = false)
    @ToString.Exclude
    private String password;

    @Column(unique = true, nullable = false, length = 100)
    @NotBlank(message = "Email is verplicht")
    @Email(message = "Ongeldig email formaat")
    private String email;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @ToString.Exclude
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private Set<EBook> ebooks = new HashSet<>();

    public static User builder() { return new User(); }

    @Builder
    public User(String username, String password, String email) {
        setUsername(username);
        setPassword(password);
        setEmail(email);
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public Set<EBook> getEbooks() {
        return Collections.unmodifiableSet(ebooks);
    }
    
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Gebruikersnaam mag niet leeg zijn");
        }
        if (username.length() > 50) {
            throw new IllegalArgumentException("Gebruikersnaam mag maximaal 50 tekens zijn");
        }
        this.username = username.trim();
    }
    
    public void setPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Wachtwoord mag niet leeg zijn");
        }
        this.password = password;
    }
    
    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("E-mail mag niet leeg zijn");
        }
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Ongeldig e-mailformaat");
        }
        this.email = email.trim().toLowerCase();
    }
    
    public void addRole(Role role) {
        if (role != null) {
            roles.add(role);
        }
    }
    
    public void removeRole(Role role) {
        if (role != null) {
            roles.remove(role);
        }
    }
    
    public void addEbook(EBook ebook) {
        if (ebook != null && !ebooks.contains(ebook)) {
            ebooks.add(ebook);
            ebook.setUser(this);
        }
    }
    
    public void removeEbook(EBook ebook) {
        if (ebook != null && ebooks.contains(ebook)) {
            ebooks.remove(ebook);
            ebook.setUser(null);
        }
    }
    
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

