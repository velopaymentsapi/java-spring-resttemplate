package com.velopayments.oa3.api;

import com.velopayments.oa3.BaseApiTest;
import com.velopayments.oa3.config.VeloConfig;
import com.velopayments.oa3.model.*;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;

@WebMvcTest()
@ContextConfiguration(classes = VeloConfig.class)
@ComponentScan(basePackages = {"com.velopayments.oa3.config"})
public class PayeeInvitationApiTest extends BaseApiTest {
    
    @Autowired
    PayeeInvitationApi payeeInvitationApi;

    @Disabled
    @Test
    void getPayeesInvitationStatusTest() {
        //todo impl
    }

    @Disabled
    @Test
    void getPayeesInvitationStatusV2Test() {
        //todo impl
    }

    @Disabled
    @Test
    void resendPayeeInviteTest() {
        //todo impl
    }

    @Test
    void v2CreatePayeeTest() {
        //random string to keep email unique
        String randomString = RandomStringUtils.randomAlphabetic(10);

        CreatePayeesRequest createPayeesRequest = new CreatePayeesRequest();
        createPayeesRequest.setPayorId(veloAPIProperties.getPayorId());
        CreatePayee createPayee = new CreatePayee();
        createPayee.setEmail("john.thompson+" + randomString + "@velopayments.com");
        createPayee.setRemoteId(randomString);
        createPayee.setType(PayeeType.INDIVIDUAL);
        CreatePayeeAddress createPayeeAddress = new CreatePayeeAddress();
        createPayeeAddress.setLine1("123 Main St");
        createPayeeAddress.setCity("St Petersburg");
        createPayeeAddress.setCountry("US");

        createPayee.setAddress(createPayeeAddress);

        createPayeesRequest.addPayeesItem(createPayee);

        CreateIndividual createIndividual = new CreateIndividual();
        IndividualV1Name individualName = new IndividualV1Name();
        individualName.setFirstName("SDKTest");
        individualName.setFirstName(randomString);
        createIndividual.setName(individualName);
        createIndividual.dateOfBirth(LocalDate.now().minusYears(21));

        createPayee.setIndividual(createIndividual);

        CreatePayeesCSVResponse response = payeeInvitationApi.v2CreatePayee(createPayeesRequest);
    }

    @Disabled
    @Test
    void v2QueryBatchStatusTest() {
    }
}
