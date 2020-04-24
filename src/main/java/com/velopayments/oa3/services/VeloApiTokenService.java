package com.velopayments.oa3.services;

import com.velopayments.oa3.api.LoginApi;
import com.velopayments.oa3.model.AuthResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class VeloApiTokenService {

    private final LoginApi loginApi;

    @Cacheable(value = "veloAuthTokenCache", sync = true)
    public String getToken() {
        log.debug("Calling Auth API");
        AuthResponse response = loginApi.veloAuth("client_credentials");
        return response.getAccessToken().toString();
    }

    @CacheEvict("veloAuthTokenCache")
    @Scheduled(fixedRate = 30000)
    public void evictCache(){
        log.debug("Evicted Auth Token Cache");
    }
}
