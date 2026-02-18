package pl.szelag.gym.config;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to mock an authenticated {@link AuthUser} in security tests.
 * This bypasses the database by injecting a pre-configured principal into the SecurityContext.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockAuthUserSecurityContextFactory.class)
public @interface WithMockAuthUser {

    /** The email address used as the username. */
    String email() default "andrzej.szelag@gym.pl";

    /** The display name of the user. */
    String fullName() default "Andrzej Szelag";

    /** The authority granted to the user (should include 'ROLE_' prefix). */
    String role() default "ROLE_ADMINISTRATOR";
}