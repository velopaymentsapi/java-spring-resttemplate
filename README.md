[![CircleCI](https://circleci.com/gh/velopaymentsapi/java-spring-resttemplate/tree/v2.20.svg?style=svg)](https://circleci.com/gh/velopaymentsapi/java-spring-resttemplate/tree/v2.20)
# Java Spring RestTemplate

The source code in this repository is generated from [Velo's OpenAPI specification](https://github.com/velopaymentsapi/VeloOpenApi).

## Requirements 
* Java 1.8 or higher
* Maven 3.6 or higher

# Usage 
## Spring Boot Properties 
To utilize the SDK, you will need to set a variety of properties. Spring Boot provides a number of mechanisms for this.
See the [Spring Boot documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/spring-boot-features.html#boot-features-external-config) for the available options.

**Note:** If using environment variables to set properties `PROPERTY_NAME` will map to `property.name`

## SDK Properties
### Velo API Properties  
The following properties must be supplied for the SDK to function properly. 

| Property | Description | 
| -------- | ----------- |
| velo.api.apikey | Your API Key. |
| velo.api.apisecret | Your API Secret. |
| velo.api.payorid | Your Payor ID. String value, UUID. |
| velo.base.url | Base URL to connect to. Velo Payments Sanbdbox URL is `https://api.sandbox.velopayments.com/`|

### Apache HTTP Client Configuration Properties
**Note**: These properties are defaulted in the Velo SDK. Override if necessary.

| Property | Description | 
| -------- | ----------- |
| velo.api.maxtotalconnections | Maximum total connections. |
| velo.api.defaultmaxperroute | Default maximum connections per route. | 
| velo.api.connectionrequesttimeout | Timeout for establishing a connection. |
| velo.api.sockettimeout | Timeout for waiting to receive data (after a connection is established. |

## Example SDK Use
In this example, we will use the Velo SDK to [obtain payouts for a payor](https://apidocs.velopayments.com/#operation/getPayoutsForPayor).

Example Spring Bean using SDK

```java
@Component
public class GetPayoutBean  {

    @Autowired
    GetPayoutApi getPayoutApi;

    @Autowired
    VeloAPIProperties veloAPIProperties;

    public GetPayoutsResponseV3 getPayoutsForPayor() {

        GetPayoutsResponseV3 getPayoutsResponseV3 = paymentAuditServiceApi.getPayoutsForPayor(veloAPIProperties.getPayorIdUuid(),
                null, null, null, null, null, null, null);

        return getPayoutsResponseV3;
    }
}
```

In the above example, two components are injected. 

The `VeloAPIProperties` component is configured with your API properties.

The component `GetPayoutApi` is the API client for the Payout API operations. In the example, the method `getPayoutsForPayor`
is called using the payor Id obtained from the `VeloAPIProperties` component. 