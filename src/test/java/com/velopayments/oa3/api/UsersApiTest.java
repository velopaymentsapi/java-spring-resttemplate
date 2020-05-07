package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class UsersApiTest {
    @Autowired
    VeloAPIProperties veloAPIProperties;

    @Autowired
    UsersApi usersApi;

    @Disabled("See MVP-9125")
    @DisplayName("Test List Users No Options")
    @Test
    void testListUsersNoOptions() {

        PagedUserResponse response = usersApi.listUsers(null, null, null, null, null, null);

        assertNotNull(response);
        assertThat(response.getContent().size()).isGreaterThan(0);
    }

    @Disabled("See MVP-9125")
    @DisplayName("Test Get User")
    @Test
    void testGetUser() {

        PagedUserResponse response = usersApi.listUsers(null, null, null, null, null, null);

        assertNotNull(response);

        UserResponse userResponse = response.getContent().get(0);

        UserResponse byIdUserResponse = usersApi.getUserByIdV2(userResponse.getId());

        assertNotNull(byIdUserResponse);

    }

    @Disabled("See MVP-9130")
    @DisplayName("Test Get Self")
    @Test
    void testGetSelf() {
        UserResponse response2 = usersApi.getSelf();

        assertNotNull(response2);
    }

    @Disabled("See MVP-9129")
    @DisplayName("Test Password Score Simple")
    @Test
    void testPasswordScoreSimple() {
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("foo");
        ValidatePasswordResponse response = usersApi.validatePasswordSelf(passwordRequest);

        assertNotNull(response);
        assertThat(response.getScore()).isEqualTo(0);
    }

    @Disabled("See MVP-9129")
    @DisplayName("Test Password Score Complex")
    @Test
    void testPasswordScoreComplex() {
        PasswordRequest passwordRequest = new PasswordRequest();
        passwordRequest.setPassword("ThisIsAComplexPassword2112Rush#1");
        ValidatePasswordResponse response = usersApi.validatePasswordSelf(passwordRequest);

        assertNotNull(response);
        assertThat(response.getScore()).isEqualTo(5);
    }


    @DisplayName("Test Invite User")
    @Test
    void testInviteUser(){
        String randomString = RandomStringUtils.randomAlphabetic(10);

        InviteUserRequest inviteUserRequest = new InviteUserRequest();
        inviteUserRequest.email("john.thompson+" + randomString + "@velopayments.com");
        inviteUserRequest.setMfaType(InviteUserRequest.MfaTypeEnum.SMS);
        inviteUserRequest.setSmsNumber("+1727872100");
        inviteUserRequest.setPrimaryContactNumber("+1727872100");
        inviteUserRequest.setRoles(Arrays.asList("payor.support"));
        inviteUserRequest.setEntityId(veloAPIProperties.getPayorIdUuid());

        ResponseEntity<Void> requestEntity = usersApi.inviteUserWithHttpInfo(inviteUserRequest);
        assertThat(requestEntity.getStatusCode().value()).isEqualTo(204);
    }
}
