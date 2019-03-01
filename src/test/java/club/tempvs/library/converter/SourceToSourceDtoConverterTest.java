package club.tempvs.library.converter;

import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static club.tempvs.library.domain.Source.Period;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.SourceDto;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.convert.converter.Converter;

import java.util.Locale;

@RunWith(MockitoJUnitRunner.class)
public class SourceToSourceDtoConverterTest {

    private Converter<Source, SourceDto> converter;

    @Mock
    private MessageSource messageSource;

    @Before
    public void setup() {
        converter = new SourceToSourceDtoConverter(messageSource);
    }

    @Test
    public void testConvert() {
        Locale locale = LocaleContextHolder.getLocale();
        String originalName = "name";
        String originalDescription = "description";
        String translatedClassification = "translated classification";
        String translatedType = "translated type";
        String translatedPeriod = "translated classification";
        Source source = new Source();
        source.setName(originalName);
        source.setDescription(originalDescription);
        source.setClassification(Classification.ARMOR);
        source.setType(Type.OTHER);
        source.setPeriod(Period.ANTIQUITY);

        when(messageSource.getMessage(Classification.ARMOR.getKey(), null, Classification.ARMOR.getKey(), locale))
                .thenReturn(translatedClassification);
        when(messageSource.getMessage(Type.OTHER.getKey(), null, Type.OTHER.getKey(), locale)).thenReturn(translatedType);
        when(messageSource.getMessage(Period.ANTIQUITY.getKey(), null, Period.ANTIQUITY.getKey(), locale)).thenReturn(translatedPeriod);

        SourceDto result = converter.convert(source);

        verify(messageSource).getMessage(Classification.ARMOR.getKey(), null, Classification.ARMOR.getKey(), locale);
        verify(messageSource).getMessage(Type.OTHER.getKey(), null, Type.OTHER.getKey(), locale);
        verify(messageSource).getMessage(Period.ANTIQUITY.getKey(), null, Period.ANTIQUITY.getKey(), locale);
        verifyNoMoreInteractions(messageSource);

        assertEquals("Name is not changed", originalName, result.getName());
        assertEquals("Description is not changed", originalDescription, result.getDescription());
        assertEquals("Classification is translated", translatedClassification, result.getClassification());
        assertEquals("Type is translated", translatedType, result.getType());
        assertEquals("Period is translated", translatedPeriod, result.getPeriod());
    }
}
