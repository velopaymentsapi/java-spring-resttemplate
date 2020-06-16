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
import lombok.Setter;

import java.util.UUID;


/**
 * POJO for holding Velo API Properties. This bean is added to the Spring Context in the VeloConfig configuration bean.
 */
@Setter
@Getter
public class VeloAPIProperties {

    private  String baseUrl;
    private  String payorId;
    private  UUID payorIdUuid;
    private  UUID apiKey;
    private  UUID apiSecret;

    public void setPayorId(String payorId) {
        this.payorId = payorId;
        this.payorIdUuid = UUID.fromString(payorId);
    }
}
