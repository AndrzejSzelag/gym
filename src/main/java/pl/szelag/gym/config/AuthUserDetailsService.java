package pl.szelag.gym.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.szelag.gym.user.entity.User;
import pl.szelag.gym.user.repository.UserRepository;

import java.util.List;

/** Adapter service bridging database user records with Spring Security authentication engine. */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * @param email unique user identifier (login)
     * UserDetails implementation for security context
     * @throws UsernameNotFoundException if user record does not exist
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.debug("Attempting to load security context for: {}", email);

        User user = userRepository.findByEmail(email.toLowerCase().trim())
                .orElseThrow(() -> {
                    log.warn("Authentication failed: User with email {} not found", email);
                    return new UsernameNotFoundException("Security error: invalid credentials");
                });

        String roleName = user.getRole().getName();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(roleName));

        return new AuthUser(user, authorities);
    }
}