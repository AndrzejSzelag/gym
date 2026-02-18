package pl.szelag.gym.common.api;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/** Domain enumeration of client membership states. */
@Getter
@RequiredArgsConstructor
public enum MembershipStatus {

    /** Client has no defined membership period. */
    NOT_ASSIGNED("membership.status.not_assigned"),

    /** Client has a valid and ongoing membership. */
    ACTIVE("membership.status.active"),

    /** Client membership period has ended. */
    EXPIRED("membership.status.expired");

    /** message bundle key for localized UI labels */
    private final String i18nKey;
}