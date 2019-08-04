package club.tempvs.library.util;

import lombok.experimental.UtilityClass;
import org.springframework.web.util.UriUtils;

import java.util.Base64;

@UtilityClass
public class Base64Helper {

    private static final String ENCODING = "UTF-8";

    public String decode(String source) {
        byte[] decodedBytes = Base64.getDecoder().decode(source);
        String decodedString = new String(decodedBytes);
        return UriUtils.decode(decodedString, ENCODING);
    }
}
