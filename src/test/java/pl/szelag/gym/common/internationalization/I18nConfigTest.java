package pl.szelag.gym.common.internationalization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class I18nConfigTest {

    private I18nConfig i18nConfig;

    @BeforeEach
    void setUp() {
        i18nConfig = new I18nConfig();
    }

    @Test
    void shouldCreateMessageSourceWithCorrectEncoding() {
        // WHEN
        MessageSource messageSource = i18nConfig.messageSource();

        // THEN
        assertThat(messageSource).isInstanceOf(ReloadableResourceBundleMessageSource.class);
        ReloadableResourceBundleMessageSource reloadable =
                (ReloadableResourceBundleMessageSource) messageSource;

        // Verify base message bundle location
        assertThat(reloadable.getBasenameSet())
                .contains("classpath:lang/messages");
    }

    @Test
    void shouldCreateLocaleResolverWithDefaultLocale() {
        // WHEN
        LocaleResolver localeResolver = i18nConfig.localeResolver();

        // THEN
        // Verify that the bean is correctly created as a SessionLocaleResolver instance.
        // Note: .isInstanceOf() implicitly performs a null check.
        assertThat(localeResolver).isInstanceOf(SessionLocaleResolver.class);
    }

    @Test
    void shouldCreateLocaleChangeInterceptorWithCorrectParam() {
        // WHEN
        LocaleChangeInterceptor interceptor = i18nConfig.localeChangeInterceptor();

        // THEN
        assertThat(interceptor.getParamName()).isEqualTo("lang");
    }

    @Test
    void shouldAddLocaleChangeInterceptorToRegistry() {
        // GIVEN
        InterceptorRegistry registry = mock(InterceptorRegistry.class);

        // WHEN
        i18nConfig.addInterceptors(registry);

        // THEN
        verify(registry).addInterceptor(any(LocaleChangeInterceptor.class));
    }
}