package pl.szelag.gym.client.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import pl.szelag.gym.common.api.MembershipStatus;

import static org.junit.jupiter.api.Assertions.*;

class ClientSubscriptionViewHelperTest {

    private ClientSubscriptionViewHelper helper;

    @BeforeEach
    void setUp() {
        // GIVEN
        helper = new ClientSubscriptionViewHelper();
    }

    @Nested
    class GetBadgeClassTests {

        @Test
        void shouldReturnSuccessBadgeForActiveStatus() {
            // WHEN
            String badgeClass = helper.getBadgeClass(MembershipStatus.ACTIVE);

            // THEN
            assertEquals("bg-success", badgeClass);
        }

        @Test
        void shouldReturnDangerBadgeForExpiredStatus() {
            // WHEN
            String badgeClass = helper.getBadgeClass(MembershipStatus.EXPIRED);

            // THEN
            assertEquals("bg-danger", badgeClass);
        }

        @Test
        void shouldReturnWarningBadgeForNotAssignedStatus() {
            // WHEN
            String badgeClass = helper.getBadgeClass(MembershipStatus.NOT_ASSIGNED);

            // THEN
            assertEquals("bg-warning text-dark", badgeClass);
        }

        @Test
        void shouldReturnSecondaryBadgeForNullStatus() {
            // WHEN
            String badgeClass = helper.getBadgeClass(null);

            // THEN
            assertEquals("bg-secondary text-white", badgeClass);
        }

        @Test
        void shouldHandleAllMembershipStatusEnumValues() {
            // GIVEN
            MembershipStatus[] statuses = MembershipStatus.values();

            // WHEN & THEN
            for (MembershipStatus status : statuses) {
                String badgeClass = helper.getBadgeClass(status);
                assertNotNull(badgeClass);
                assertFalse(badgeClass.isBlank());
            }
        }
    }

    @Nested
    class GetDaysBadgeClassTests {

        @Test
        void shouldReturnSuccessBadgeForPositiveDays() {
            // WHEN
            String badgeClass = helper.getDaysBadgeClass(10L);

            // THEN
            assertEquals("bg-success", badgeClass);
        }

        @Test
        void shouldReturnSuccessBadgeForZeroDays() {
            // WHEN
            String badgeClass = helper.getDaysBadgeClass(0L);

            // THEN
            assertEquals("bg-success", badgeClass);
        }

        @Test
        void shouldReturnDangerBadgeForNegativeDays() {
            // WHEN
            String badgeClass = helper.getDaysBadgeClass(-5L);

            // THEN
            assertEquals("bg-danger", badgeClass);
        }

        @Test
        void shouldReturnSecondaryBadgeForNullDays() {
            // WHEN
            String badgeClass = helper.getDaysBadgeClass(null);

            // THEN
            assertEquals("bg-secondary text-white", badgeClass);
        }

        @Test
        void shouldHandleBoundaryValues() {
            // WHEN
            String oneDay = helper.getDaysBadgeClass(1L);
            String minusOneDay = helper.getDaysBadgeClass(-1L);

            // THEN
            assertEquals("bg-success", oneDay);
            assertEquals("bg-danger", minusOneDay);
        }
    }

    @Nested
    class EdgeCasesAndConsistencyTests {

        @Test
        void shouldConsistentlyReturnBootstrapCompatibleClasses() {
            // GIVEN
            MembershipStatus[] statuses = MembershipStatus.values();
            Long[] daysValues = {-100L, 0L, 100L, null};

            // WHEN & THEN
            for (MembershipStatus status : statuses) {
                assertTrue(helper.getBadgeClass(status).startsWith("bg-"));
            }

            for (Long days : daysValues) {
                assertTrue(helper.getDaysBadgeClass(days).startsWith("bg-"));
            }
        }

        @Test
        void shouldDifferentiateBetweenStatusTypes() {
            // GIVEN
            // Helper initialized in setUp

            // WHEN
            String active = helper.getBadgeClass(MembershipStatus.ACTIVE);
            String expired = helper.getBadgeClass(MembershipStatus.EXPIRED);
            String notAssigned = helper.getBadgeClass(MembershipStatus.NOT_ASSIGNED);

            // THEN
            assertNotEquals(active, expired);
            assertNotEquals(active, notAssigned);
            assertNotEquals(expired, notAssigned);
        }

        @Test
        void shouldDifferentiateBetweenPositiveAndNegativeDays() {
            // GIVEN
            Long pos = 10L;
            Long neg = -10L;

            // WHEN
            String posBadge = helper.getDaysBadgeClass(pos);
            String negBadge = helper.getDaysBadgeClass(neg);

            // THEN
            assertNotEquals(posBadge, negBadge);
        }
    }
}