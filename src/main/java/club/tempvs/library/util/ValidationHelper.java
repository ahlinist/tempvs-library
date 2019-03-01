package club.tempvs.library.util;

import club.tempvs.library.dto.ErrorsDto;

public interface ValidationHelper {

    ErrorsDto getErrors();

    void addError(ErrorsDto errorsDto, String field, String messageKey);

    void addError(ErrorsDto errorsDto, String field, String messageKey, Object[] args);

    void processErrors(ErrorsDto errorsDto);
}
