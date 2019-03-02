package club.tempvs.library.converter;

import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.SourceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
public class SourceToSourceDtoConverter implements Converter<Source, SourceDto> {

    private final MessageSource messageSource;

    @Override
    public SourceDto convert(Source source) {
        Locale locale = LocaleContextHolder.getLocale();
        String classificationKey = source.getClassification().getKey();
        String typeKey = source.getType().getKey();
        String periodKey = source.getPeriod().getKey();
        String classification = messageSource.getMessage(classificationKey, null, classificationKey, locale);
        String type = messageSource.getMessage(typeKey, null, typeKey, locale);
        String period = messageSource.getMessage(periodKey, null, periodKey, locale);

        SourceDto sourceDto = new SourceDto();
        sourceDto.setId(source.getId());
        sourceDto.setName(source.getName());
        sourceDto.setDescription(source.getDescription());
        sourceDto.setClassification(classification);
        sourceDto.setType(type);
        sourceDto.setPeriod(period);
        return sourceDto;
    }
}
