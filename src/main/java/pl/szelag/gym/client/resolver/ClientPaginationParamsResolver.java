package pl.szelag.gym.client.resolver;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.metamodel.Attribute;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import pl.szelag.gym.client.dto.PaginationParams;
import pl.szelag.gym.client.entity.Client;

import java.util.Set;
import java.util.stream.Collectors;

/** Resolves and validates PaginationParams using a dynamic whitelist from JPA Metamodel. */
@Component
@RequiredArgsConstructor
@Slf4j
public class ClientPaginationParamsResolver implements HandlerMethodArgumentResolver {

    private final Validator validator;
    private final EntityManager entityManager;
    private Set<String> allowedSortFields;

    /** Initializes allowedSortFields whitelist from Client entity basic attributes. */
    @PostConstruct
    public void init() {
        allowedSortFields = extractEntityFields(Client.class);
        log.info("Pagination whitelist for Client initialized: {}", allowedSortFields);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return PaginationParams.class.equals(parameter.getParameterType());
    }

    /** validated PaginationParams object resolved from web request */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        PaginationParams params = new PaginationParams();
        params.setKeyword(validateKeyword(webRequest.getParameter("keyword")));
        params.setSortField(validateSortField(webRequest.getParameter("sortField")));
        params.setSortDirection(validateSortDirection(webRequest.getParameter("sortDirection")));

        params.setPageNo(validateInt(webRequest.getParameter("pageNo"),
                PaginationParams.MIN_PAGE_NO, PaginationParams.MAX_PAGE_NO, PaginationParams.MIN_PAGE_NO));

        params.setPageSize(validateInt(webRequest.getParameter("pageSize"),
                PaginationParams.MIN_PAGE_SIZE, PaginationParams.MAX_PAGE_SIZE, PaginationParams.DEFAULT_PAGE_SIZE));

        Set<ConstraintViolation<PaginationParams>> violations = validator.validate(params);
        if (!violations.isEmpty()) {
            violations.forEach(v
                    -> log.warn("Invalid pagination param: {}={}", v.getPropertyPath(), v.getInvalidValue()));
        }
        return params;
    }

    /** @param input raw keyword trimmed string or empty */
    private String validateKeyword(String input) {
        return StringUtils.hasText(input) ? input.trim() : "";
    }

    /** @param input raw field name whitelisted field or default to prevent SQL injection */
    private String validateSortField(String input) {
        if (!StringUtils.hasText(input) || allowedSortFields == null) return PaginationParams.DEFAULT_SORT_FIELD;
        String trimmed = input.trim();
        return allowedSortFields.contains(trimmed) ? trimmed : PaginationParams.DEFAULT_SORT_FIELD;
    }

    /** @param input raw direction 'asc', 'desc' or default */
    private String validateSortDirection(String input) {
        String dir = (input != null) ? input.trim().toLowerCase() : "";
        return ("asc".equals(dir) || "desc".equals(dir)) ? dir : PaginationParams.DEFAULT_SORT_DIRECTION;
    }

    /** @param input raw string @param min minimum @param max maximum @param defaultValue fallback parsed int */
    private int validateInt(String input, int min, int max, int defaultValue) {
        try {
            int value = Integer.parseInt(input != null ? input.trim() : "");
            return Math.clamp(value, min, max);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /** @param entityClass JPA entity class set of basic persistent attribute names */
    private Set<String> extractEntityFields(Class<?> entityClass) {
        Metamodel metamodel = entityManager.getMetamodel();
        EntityType<?> entityType = metamodel.entity(entityClass);
        return entityType.getAttributes().stream()
                .filter(attr ->
                        attr.getPersistentAttributeType() == Attribute.PersistentAttributeType.BASIC)
                .map(Attribute::getName)
                .collect(Collectors.toSet());
    }
}