package pl.szelag.gym.config;

import lombok.Getter;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.szelag.gym.user.entity.User;

import java.util.Collection;

/**
 * Custom UserDetails implementation acting as a secure session-resident representation
 * of the authenticated user within the Spring Security context.
 */
@Getter
public class AuthUser implements UserDetails, CredentialsContainer {

    /** internal database identifier */
    private final Long id;

    /** combined first and last name for UI display */
    private final String fullName;

    /** primary email address used as login username */
    private final String email;

    /** encoded password hash; nullified after authentication */
    private String password;

    /** collection of granted authorities (roles) */
    private final Collection<? extends GrantedAuthority> authorities;

    /** status of the user account */
    private final boolean enabled;

    /** @param user domain entity @param authorities resolved Spring Security roles */
    public AuthUser(User user, Collection<? extends GrantedAuthority> authorities) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = authorities;
        this.enabled = true;
    }

    /** Wipes sensitive credentials from memory after the authentication process is complete. */
    @Override
    public void eraseCredentials() {
        this.password = null;
    }

    /** the email address as the unique security identifier */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }
}