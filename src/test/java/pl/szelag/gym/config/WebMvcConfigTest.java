package pl.szelag.gym.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import pl.szelag.gym.client.resolver.ClientPaginationParamsResolver;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for WebMvcConfig.
 * Ensures that custom argument resolvers are correctly registered in the Spring MVC context.
 */
class WebMvcConfigTest {

    @Test
    void shouldRegisterClientPaginationParamsResolver() {
        // GIVEN
        ClientPaginationParamsResolver resolver = mock(ClientPaginationParamsResolver.class);
        WebMvcConfig config = new WebMvcConfig(resolver);
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        // WHEN
        config.addArgumentResolvers(resolvers);

        // THEN
        assertThat(resolvers)
                .as("WebMvcConfig must add ClientPaginationParamsResolver to the list of MVC resolvers")
                .contains(resolver)
                .hasSize(1);
    }
}