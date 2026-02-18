package pl.szelag.gym.client.resolver;

import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.entity.Client;

import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // This fixes the PotentialStubbingProblem
class ClientPaginationParamsResolverTest {

    @Mock
    private Validator validator;

    @Mock
    private EntityManager entityManager;

    @Mock
    private NativeWebRequest webRequest;

    @InjectMocks
    private ClientPaginationParamsResolver resolver;

    @BeforeEach
    void setUp() {
        // GIVEN
        Metamodel metamodel = mock(Metamodel.class);
        EntityType<Client> entityType = mock(EntityType.class);
        Attribute<Client, ?> emailAttr = mock(Attribute.class);
        Attribute<Client, ?> lastNameAttr = mock(Attribute.class);

        when(entityManager.getMetamodel()).thenReturn(metamodel);
        when(metamodel.entity(Client.class)).thenReturn(entityType);

        when(emailAttr.getName()).thenReturn("email");
        when(emailAttr.getPersistentAttributeType()).thenReturn(Attribute.PersistentAttributeType.BASIC);
        when(lastNameAttr.getName()).thenReturn("lastName");
        when(lastNameAttr.getPersistentAttributeType()).thenReturn(Attribute.PersistentAttributeType.BASIC);

        when(entityType.getAttributes()).thenReturn(Set.of(emailAttr, lastNameAttr));

        resolver.init();
    }

    @Test
    void shouldResolveValidPaginationParams() {
        // GIVEN
        when(webRequest.getParameter("keyword")).thenReturn("Andrzej");
        when(webRequest.getParameter("pageNo")).thenReturn("1");
        when(webRequest.getParameter("pageSize")).thenReturn("20");
        when(webRequest.getParameter("sortField")).thenReturn("email");
        when(webRequest.getParameter("sortDirection")).thenReturn("desc");
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        // WHEN
        PaginationParams result = (PaginationParams) resolver.resolveArgument(null, null, webRequest, null);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getKeyword()).isEqualTo("Andrzej");
        assertThat(result.getSortField()).isEqualTo("email");
    }

    @Test
    void shouldFallbackToDefaultsOnInvalidInput() {
        // GIVEN: Keyword is not mocked, but LENIENT mode allows it to return null/default
        when(webRequest.getParameter("sortField")).thenReturn("SQL_INJECTION_ATTEMPT");
        when(webRequest.getParameter("pageNo")).thenReturn("not-a-number");
        when(webRequest.getParameter("sortDirection")).thenReturn("invalid");
        when(validator.validate(any())).thenReturn(Collections.emptySet());

        // WHEN
        PaginationParams result = (PaginationParams) resolver.resolveArgument(null, null, webRequest, null);

        // THEN
        assertThat(result.getSortField()).isEqualTo(PaginationParams.DEFAULT_SORT_FIELD);
        assertThat(result.getPageNo()).isEqualTo(PaginationParams.MIN_PAGE_NO);
        assertThat(result.getSortDirection()).isEqualTo(PaginationParams.DEFAULT_SORT_DIRECTION);
    }

    @Test
    void shouldSupportPaginationParamsClass() {
        // GIVEN
        MethodParameter param = mock(MethodParameter.class);
        doReturn(PaginationParams.class).when(param).getParameterType();

        // WHEN
        boolean supports = resolver.supportsParameter(param);

        // THEN
        assertThat(supports).isTrue();
    }
}