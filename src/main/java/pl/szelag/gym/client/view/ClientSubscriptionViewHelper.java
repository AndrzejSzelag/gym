package pl.szelag.gym.client.view;

import org.springframework.stereotype.Component;
import pl.szelag.gym.common.api.MembershipStatus;

/** UI helper for mapping domain subscription statuses and urgency levels to CSS styling classes. */
@Component
public class ClientSubscriptionViewHelper {

    /** @param status domain membership status Bootstrap badge CSS class string */
    public String getBadgeClass(MembershipStatus status) {
        if (status == null) {
            return "bg-secondary text-white";
        }

        return switch (status) {
            case ACTIVE -> "bg-success";
            case EXPIRED -> "bg-danger";
            case NOT_ASSIGNED -> "bg-warning text-dark";
        };
    }

    /** @param days number of days remaining (positive) or overdue (negative) CSS class based on urgency */
    public String getDaysBadgeClass(Long days) {
        if (days == null) {
            return "bg-secondary text-white";
        }
        return days >= 0 ? "bg-success" : "bg-danger";
    }
}