package pl.szelag.gym.client.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** Encapsulates pagination, sorting, and search state for client requests. */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class PaginationParams {

    public static final int MIN_PAGE_NO = 1;
    public static final int MAX_PAGE_NO = 1000;
    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 50;

    public static final int DEFAULT_PAGE_SIZE = 10;
    public static final String DEFAULT_SORT_FIELD = "expirationDate";
    public static final String DEFAULT_SORT_DIRECTION = "desc";

    /** current page number (1-based) */
    @Min(value = MIN_PAGE_NO, message = "{validation.pageNo.min}")
    @Max(value = MAX_PAGE_NO, message = "{validation.pageNo.max}")
    private Integer pageNo = MIN_PAGE_NO;

    /** number of records per page */
    @Min(value = MIN_PAGE_SIZE, message = "{validation.pageSize.min}")
    @Max(value = MAX_PAGE_SIZE, message = "{validation.pageSize.max}")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    /** database field name used for sorting */
    @Pattern(regexp = "^[a-zA-Z][a-zA-Z0-9]*$", message = "{validation.sortField.pattern}")
    private String sortField = DEFAULT_SORT_FIELD;

    /** sort order: asc or desc */
    @Pattern(regexp = "^(?i)(asc|desc)$", message = "{validation.sortDirection.pattern}")
    private String sortDirection = DEFAULT_SORT_DIRECTION;

    private String keyword = "";

    /** inverted direction for UI toggle links */
    public String getReverseSortDirection() {
        return "asc".equalsIgnoreCase(sortDirection) ? "desc" : "asc";
    }

    /** true if search keyword is present and not blank */
    public boolean isSearchActive() {
        return keyword != null && !keyword.trim().isEmpty();
    }

    /** trimmed search keyword or empty string if null */
    public String getKeyword() {
        return keyword != null ? keyword.trim() : "";
    }
}