package club.tempvs.library.converter;

import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.util.Base64Helper;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FindSourceDtoConverter implements Converter<String, FindSourceDto> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public FindSourceDto convert(String source) {
        String decoded = Base64Helper.decode(source);
        return objectMapper.readValue(decoded, FindSourceDto.class);
    }
}
