package pl.szelag.gym.client.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaginationParamsTest {

    @Test
    void shouldReturnDefaultValuesOnInitialization() {
        // GIVEN
        PaginationParams params;

        // WHEN
        params = new PaginationParams();

        // THEN
        assertThat(params.getPageNo()).isEqualTo(1);
        assertThat(params.getPageSize()).isEqualTo(10);
        assertThat(params.getSortField()).isEqualTo("expirationDate");
        assertThat(params.getSortDirection()).isEqualTo("desc");
    }

    @Test
    void shouldToggleSortDirectionCorrectly() {
        // GIVEN
        PaginationParams params = new PaginationParams();

        // WHEN & THEN (Case 1: desc -> asc)
        params.setSortDirection("desc");
        assertThat(params.getReverseSortDirection()).isEqualTo("asc");

        // WHEN & THEN (Case 2: asc -> desc)
        params.setSortDirection("asc");
        assertThat(params.getReverseSortDirection()).isEqualTo("desc");
    }

    @Test
    void shouldHandleSearchStatusAndKeywordNormalization() {
        // GIVEN
        PaginationParams params = new PaginationParams();

        // WHEN (Empty keyword)
        params.setKeyword("  ");

        // THEN
        assertThat(params.isSearchActive()).isFalse();

        // WHEN (Valid keyword with spaces)
        params.setKeyword("  Andrzej  ");

        // THEN
        assertThat(params.isSearchActive()).isTrue();
        assertThat(params.getKeyword()).isEqualTo("Andrzej");
    }
}