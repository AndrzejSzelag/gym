package pl.szelag.gym.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

/** Security configuration for administration panel access, session hardening, and CORS/CSRF protection. */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /** Base URI for the login page. */
    private static final String LOGIN = "/login";

    /** URI used to redirect users when their session has expired. */
    public static final String LOGIN_TIMEOUT_URI = "/login?timeout=true";

    /** URI used to redirect users after a successful logout. */
    public static final String LOGOUT_SUCCESS_URI = "/login?logout=true";

    private final AuthUserDetailsService authUserDetailsService;

    /** provider linking password encoder with custom user details service */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(authUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /** @param http security builder chain requiring HTTPS for all channels @throws Exception on config error */
    @Bean
    @Profile("!test-local")
    public SecurityFilterChain securityFilterChainWithHttps(HttpSecurity http) throws Exception {
        http.requiresChannel(c -> c.anyRequest().requiresSecure());
        return configure(http);
    }

    /** @param http security builder chain without mandatory HTTPS for local development @throws Exception */
    @Bean
    @Profile("test-local")
    public SecurityFilterChain securityFilterChainWithoutHttps(HttpSecurity http) throws Exception {
        return configure(http);
    }

    /** Core security logic: URL authorization, session management, and UI protection headers. */
    private SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/users/**", "/clients/**").hasAuthority("ROLE_ADMINISTRATOR")
                        .requestMatchers(LOGIN,
                                "/register",
                                "/register/save",
                                "/fonts/**",
                                "/css/**",
                                "/js/**",
                                "/img/**").permitAll()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(f -> f
                        .loginPage(LOGIN)
                        .defaultSuccessUrl("/clients", true)
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(l -> l
                        .logoutUrl("/logout")
                        .logoutSuccessUrl(LOGOUT_SUCCESS_URI)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .sessionManagement(s -> s
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation(sf -> sf.migrateSession())
                        .maximumSessions(1)
                        .expiredUrl(LOGIN_TIMEOUT_URI)
                )
                .exceptionHandling(e
                        -> e.authenticationEntryPoint(customAuthEntryPoint()))
                .headers(h -> h
                        .frameOptions(f -> f.deny())
                        .httpStrictTransportSecurity(hsts -> hsts
                                .maxAgeInSeconds(31536000)
                                .includeSubDomains(true))
                )
                .csrf(c -> c.csrfTokenRepository(new HttpSessionCsrfTokenRepository()));

        return http.build();
    }

    /** entry point returning 401 for AJAX/Fetch and redirects for standard browser requests */
    @Bean
    public AuthenticationEntryPoint customAuthEntryPoint() {
        return (req, res, ex) -> {
            String requestedWith = req.getHeader("X-Requested-With");
            String fetchMode = req.getHeader("Sec-Fetch-Mode");
            boolean isAsync = "XMLHttpRequest".equals(requestedWith) || "fetch".equalsIgnoreCase(fetchMode);

            if (isAsync) {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            } else {
                res.sendRedirect(!req.isRequestedSessionIdValid() ? LOGIN_TIMEOUT_URI : LOGIN);
            }
        };
    }

    /** standard BCrypt implementation for password hashing */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** @param cfg authentication configuration manager for manual auth triggers @throws Exception */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }
}