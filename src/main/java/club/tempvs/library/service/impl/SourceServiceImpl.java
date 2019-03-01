package club.tempvs.library.service.impl;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static java.util.Objects.isNull;

import club.tempvs.library.dto.ErrorsDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.exception.ForbiddenException;
import club.tempvs.library.dao.SourceRepository;
import club.tempvs.library.domain.Source;
import club.tempvs.library.domain.User;
import club.tempvs.library.holder.UserHolder;
import club.tempvs.library.model.Role;
import club.tempvs.library.service.SourceService;
import club.tempvs.library.util.ValidationHelper;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SourceServiceImpl implements SourceService {

    private static final String NAME_FIELD = "name";
    private static final String CLASSIFICATION_FIELD = "classification";
    private static final String PERIOD_FIELD = "period";
    private static final String TYPE_FIELD = "type";
    private static final String NAME_BLANK_ERROR = "source.name.blank.error";
    private static final String CLASSIFICATION_MISSING_ERROR = "source.classification.missing.error";
    private static final String PERIOD_MISSING_ERROR = "source.period.missing.error";
    private static final String TYPE_MISSING_ERROR = "source.type.missing.error";

    private final SourceRepository sourceRepository;
    private final ValidationHelper validationHelper;
    private final UserHolder userHolder;
    private final ConversionService conversionService;

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public SourceDto create(SourceDto sourceDto) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles =
                Arrays.asList(Role.ROLE_ADMIN, Role.ROLE_ARCHIVARIUS, Role.ROLE_SCRIBE, Role.ROLE_CONTRIBUTOR);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("User lacks the necessary authorities to create a source");
        }

        ErrorsDto errorsDto = validationHelper.getErrors();

        if (isBlank(sourceDto.getName())) {
            validationHelper.addError(errorsDto, NAME_FIELD, NAME_BLANK_ERROR);
        }

        if (isNull(sourceDto.getClassification())) {
            validationHelper.addError(errorsDto, CLASSIFICATION_FIELD, CLASSIFICATION_MISSING_ERROR);
        }

        if (isNull(sourceDto.getPeriod())) {
            validationHelper.addError(errorsDto, PERIOD_FIELD, PERIOD_MISSING_ERROR);
        }

        if (isNull(sourceDto.getPeriod())) {
            validationHelper.addError(errorsDto, TYPE_FIELD, TYPE_MISSING_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        Source source = sourceDto.toSource();
        Source persistentSource = sourceRepository.save(source);
        return conversionService.convert(persistentSource, SourceDto.class);
    }

    @Override
    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    public SourceDto get(Long id) {
        return sourceRepository.findById(id)
                .map(source -> conversionService.convert(source, SourceDto.class))
                .get();
    }
}
