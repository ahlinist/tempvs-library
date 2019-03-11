package club.tempvs.library.util;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

public interface RestCaller {

    <T> T call(String url, HttpMethod method, T payload, Class<T> responseType);
}
