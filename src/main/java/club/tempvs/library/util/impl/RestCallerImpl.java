package club.tempvs.library.util.impl;

import club.tempvs.library.util.RestCaller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class RestCallerImpl implements RestCaller {

    private static final String AUTHORIZATION_HEADER= "Authorization";

    private final RestTemplate restTemplate;

    public <T> T call(String url, HttpMethod method, T payload, Class<T> responseType) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String authHeaderValue = request.getHeader(AUTHORIZATION_HEADER);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION_HEADER, authHeaderValue);
        HttpEntity<T> httpEntity = new HttpEntity(payload, httpHeaders);

        return restTemplate.exchange(url, method, httpEntity, responseType).getBody();
    }
}
