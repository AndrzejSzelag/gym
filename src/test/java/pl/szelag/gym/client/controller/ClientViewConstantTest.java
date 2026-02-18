package pl.szelag.gym.client.controller;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;

import static org.assertj.core.api.Assertions.assertThat;

class ClientViewConstantTest {

    @Test
    void shouldVerifyConstantsValues() {
        // GIVEN & WHEN & THEN
        assertThat(ClientViewConstant.CLIENT).isEqualTo("client");
        assertThat(ClientViewConstant.NEW_CLIENT).isEqualTo("newClient");
        assertThat(ClientViewConstant.CLIENTS).isEqualTo("clients");
    }

    @Test
    void shouldCoverPrivateConstructorForJacoco() throws Exception {
        // GIVEN
        Constructor<ClientViewConstant> constructor = ClientViewConstant.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // WHEN
        ClientViewConstant instance = constructor.newInstance();

        // THEN
        // Ensure the instance is correctly created and of the expected type
        assertThat(instance).isInstanceOf(ClientViewConstant.class);
    }
}