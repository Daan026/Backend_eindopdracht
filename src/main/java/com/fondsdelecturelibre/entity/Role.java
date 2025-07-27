package com.fondsdelecturelibre.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Getter
@Setter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false, unique = true)
    private ERole name;

    public Role(ERole name) {
        this.name = java.util.Objects.requireNonNull(name, "Rolnaam mag niet null zijn");
    }
    
    public void setName(ERole name) {
        this.name = java.util.Objects.requireNonNull(name, "Rolnaam mag niet null zijn");
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Role role)) return false;
        return id != null && id.equals(role.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public enum ERole {
        ROLE_GUEST,
        
        ROLE_MEMBER,
        
        ROLE_ADMIN;
        
        public static ERole fromString(String role) {
            if (role == null) {
                throw new IllegalArgumentException("Rol mag niet null zijn");
            }
            try {
                return ERole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Ongeldige rol: " + role, e);
            }
        }
    }
}
