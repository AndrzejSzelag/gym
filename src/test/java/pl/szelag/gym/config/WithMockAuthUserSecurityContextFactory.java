package pl.szelag.gym.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import pl.szelag.gym.user.entity.RoleFactory;
import pl.szelag.gym.user.entity.User;

import java.util.List;

/**
 * Factory responsible for bridging the {@link WithMockAuthUser} annotation with
 * Spring Security's context by creating a mock {@link AuthUser} principal.
 */
public class WithMockAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithMockAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithMockAuthUser annotation) {
        // GIVEN: Empty security context
        SecurityContext context = SecurityContextHolder.createEmptyContext();

        // WHEN: Parsing annotation data to create domain-compatible entities
        var role = RoleFactory.systemRole(annotation.role());

        // Split full name into first and last name for the User constructor
        String[] nameParts = annotation.fullName().split(" ", 2);
        String firstName = nameParts[0];
        String lastName = (nameParts.length > 1) ? nameParts[1] : "";

        var user = new User(
                firstName,
                lastName,
                annotation.email(),
                "protected_password",
                role
        );

        // Create the custom principal used by the application
        var principal = new AuthUser(user, List.of(new SimpleGrantedAuthority(annotation.role())));

        var auth = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                principal.getAuthorities()
        );

        // THEN: Inject the authentication into the context
        context.setAuthentication(auth);
        return context;
    }
}