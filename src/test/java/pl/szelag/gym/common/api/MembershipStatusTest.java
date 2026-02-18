package pl.szelag.gym.common.api;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MembershipStatusTest {

    @Test
    void shouldHaveCorrectI18nKeys() {
        // GIVEN
        // Enum values are static

        // WHEN & THEN
        assertEquals("membership.status.not_assigned", MembershipStatus.NOT_ASSIGNED.getI18nKey());
        assertEquals("membership.status.active", MembershipStatus.ACTIVE.getI18nKey());
        assertEquals("membership.status.expired", MembershipStatus.EXPIRED.getI18nKey());
    }

    @Test
    void shouldReturnAllEnumValues() {
        // GIVEN
        // MembershipStatus enum

        // WHEN
        MembershipStatus[] statuses = MembershipStatus.values();

        // THEN
        assertEquals(3, statuses.length);
        assertNotNull(MembershipStatus.valueOf("ACTIVE"));
        assertNotNull(MembershipStatus.valueOf("EXPIRED"));
        assertNotNull(MembershipStatus.valueOf("NOT_ASSIGNED"));
    }

    @Test
    void shouldMatchEnumNameWithI18nSuffix() {
        // GIVEN
        MembershipStatus active = MembershipStatus.ACTIVE;

        // WHEN
        String key = active.getI18nKey();

        // THEN
        // Verifying if the key follows the convention: membership.status.[name_lowercase]
        assertEquals("membership.status." + active.name().toLowerCase(), key);
    }
}