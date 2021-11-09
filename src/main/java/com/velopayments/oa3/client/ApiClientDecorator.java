package com.velopayments.oa3.client;

import com.velopayments.oa3.services.VeloApiTokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
public class ApiClientDecorator extends ApiClient {

    private final VeloApiTokenService veloApiTokenService;

    public ApiClientDecorator(RestTemplate restTemplate, VeloApiTokenService veloApiTokenService) {
        super(restTemplate);
        this.veloApiTokenService = veloApiTokenService;
    }

    @Override
    public <T> ResponseEntity<T> invokeAPI(String path, HttpMethod method, Map<String, Object> pathParams, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType, String[] authNames, ParameterizedTypeReference<T> returnType) throws RestClientException {
        this.setAccessToken(veloApiTokenService.getToken());

        try{
            return super.invokeAPI(path, method, pathParams, queryParams, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
        } catch (HttpClientErrorException e){

            if( e.getStatusCode() == HttpStatus.UNAUTHORIZED){

                log.debug("failed auth, getting new token");
                return this.invokeAPIReauth(path, method, pathParams, queryParams, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
            } else {
                throw e;
            }
        }
    }

    private synchronized <T> ResponseEntity<T> invokeAPIReauth(String path, HttpMethod method, Map<String, Object> pathParams, MultiValueMap<String, String> queryParams, Object body, HttpHeaders headerParams, MultiValueMap<String, String> cookieParams, MultiValueMap<String, Object> formParams, List<MediaType> accept, MediaType contentType, String[] authNames, ParameterizedTypeReference<T> returnType) {
        veloApiTokenService.evictCache();
        this.setAccessToken(veloApiTokenService.getToken());
        return super.invokeAPI(path, method, pathParams, queryParams, body, headerParams, cookieParams, formParams, accept, contentType, authNames, returnType);
    }
}
