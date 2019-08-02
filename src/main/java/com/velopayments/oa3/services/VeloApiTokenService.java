package com.velopayments.oa3.services;

import com.velopayments.oa3.api.AuthApi;
import com.velopayments.oa3.model.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class VeloApiTokenService {

    private final AuthApi authApi;

    public String getToken() {
        AuthResponse response = authApi.veloAuth("client_credentials");
        return response.getAccessToken().toString();
    }
}
