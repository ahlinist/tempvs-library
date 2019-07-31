package club.tempvs.library.converter;

import club.tempvs.library.dto.FindSourceDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@RequiredArgsConstructor
public class FindSourceDtoConverter implements Converter<String, FindSourceDto> {

    private final ObjectMapper objectMapper;

    @Override
    public FindSourceDto convert(String q) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(q);
            String decodedString = new String(decodedBytes);
            return objectMapper.readValue(decodedString, FindSourceDto.class);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
