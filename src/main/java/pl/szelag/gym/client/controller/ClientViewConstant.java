package pl.szelag.gym.client.controller;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/** Shared constants for Client UI attributes, model keys, and Thymeleaf view names. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ClientViewConstant {

    /** Model attribute key for an existing client being edited. */
    public static final String CLIENT = "client";

    /** Model attribute key for the registration/add-client form. */
    public static final String NEW_CLIENT = "newClient";

    /** Thymeleaf template name for the clients list and management dashboard. */
    public static final String CLIENTS = "clients";

}