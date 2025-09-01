package com.fondsdelecturelibre.service;

import com.fondsdelecturelibre.dtos.UserDto;
import com.fondsdelecturelibre.entity.Role;
import com.fondsdelecturelibre.entity.User;
import com.fondsdelecturelibre.exception.DuplicateResourceException;
import com.fondsdelecturelibre.repository.RoleRepository;
import com.fondsdelecturelibre.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
    }

    public UserDto createUser(UserDto userDto) {
        if (existsByUsername(userDto.getUsername())) {
            throw new DuplicateResourceException("Gebruikersnaam bestaat al: " + userDto.getUsername());
        }
        
        if (existsByEmail(userDto.getEmail())) {
            throw new DuplicateResourceException("Email adres bestaat al: " + userDto.getEmail());
        }
        
        // Automatisch MEMBER rol toewijzen aan nieuwe gebruikers
        Role memberRole = roleRepository.findByName(Role.ERole.ROLE_MEMBER)
            .orElseGet(() -> roleRepository.save(new Role(Role.ERole.ROLE_MEMBER)));
        
        User user = new User();
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRoles(Set.of(memberRole));
        
        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    public UserDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return convertToDto(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).toList()
        );
    }

    public UserDto convertToDto(User user) {
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }
}
