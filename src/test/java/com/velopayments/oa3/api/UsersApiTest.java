package com.velopayments.oa3.api;

import com.velopayments.oa3.VeloAPIProperties;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.PagedUserResponse;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

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

    @Disabled // missing enum value on MfaStatus
    @DisplayName("Test List Users No Options")
    @Test
    void testListUsersNoOptions() {

        PagedUserResponse response = usersApi.listUsers(null, null, null, null, null, null);

        assertNotNull(response);
        assertThat(response.getContent().size()).isGreaterThan(0);
    }
}
