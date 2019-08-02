/*
 *
 *  * Copyright 2018 the original author or authors.
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.velopayments.oa3;

import lombok.Getter;

@Getter
public class VeloAPIProperties {

    private final String baseUrl;
    private final String payorId;

    public VeloAPIProperties(String baseUrl, String payorId) {
        this.baseUrl = baseUrl;
        this.payorId = payorId;
    }

    public static final String AUTH_URL = "/v1/authenticate";

    public static final String GET_PAYOR_URL = "/v1/payors/{payorId}";

    public static final String GET_PAYEES_URL = "/v1/payees";

    public static final String GET_PAYOUTS_V3_URL = "/v3/paymentaudit/payouts";

    public static final String GET_PAYMENTS_FOR_PAYOUT_V3_URL = "/v3/paymentaudit/payouts/{payoutId}";

    public static final String GET_PAYMENT_V3_URL = "/v3/paymentaudit/payments/{paymentId}";

    public static final String GET_PAYOUT_SUMMARY_URL = "/v3/payouts/{payoutId}";
}
