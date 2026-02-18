package pl.szelag.gym.user.identity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

/** Domain enumeration of system roles and security levels. */
public enum UserRole {

    ADMINISTRATOR("ADMINISTRATOR"),
    GUEST("GUEST");

    private final String roleName;

    UserRole(String roleName) {
        this.roleName = roleName;
    }

    /** raw role name without prefix */
    public String getRoleName() {
        return roleName;
    }

    /** authority name with 'ROLE_' prefix for Spring Security */
    public String authority() {
        return "ROLE_" + roleName;
    }

    /** GrantedAuthority implementation for security context */
    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(authority());
    }

    @Override
    public String toString() {
        return roleName;
    }
}