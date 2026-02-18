package pl.szelag.gym.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import pl.szelag.gym.client.resolver.ClientPaginationParamsResolver;

import java.util.List;

/** Custom Web MVC configuration for registering specialized argument resolvers. */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    /** Pagination resolver, lazy-loaded to prevent circular dependencies
     * and ensure persistence context is ready before metamodel extraction. */
    @Lazy
    private final ClientPaginationParamsResolver paginationParamsResolver;

    /** @param resolvers list of built-in and custom argument resolvers */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(paginationParamsResolver);
    }
}