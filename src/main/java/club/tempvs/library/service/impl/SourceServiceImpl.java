package club.tempvs.library.service.impl;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static club.tempvs.library.domain.Source.Period;
import static club.tempvs.library.domain.Source.Classification;
import static club.tempvs.library.domain.Source.Type;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.CollectionUtils.isEmpty;
import static club.tempvs.library.model.Role.*;

import club.tempvs.library.clients.ImageClient;
import club.tempvs.library.dto.ErrorsDto;
import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.ImageDto;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;

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
    private final ImageClient imageClient;

    @Override
    public SourceDto create(SourceDto sourceDto) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles =
                Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE, ROLE_CONTRIBUTOR);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
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

        if (isNull(sourceDto.getType())) {
            validationHelper.addError(errorsDto, TYPE_FIELD, TYPE_MISSING_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        Source source = sourceDto.toSource();
        return saveSource(source).toSourceDto();
    }

    public SourceDto get(Long id) {
        return getSource(id)
                .map(Source::toSourceDto)
                .get();
    }

    @Override
    public List<SourceDto> find(FindSourceDto findSourceDto, int page, int size) {
        String query = findSourceDto.getQuery();

        if (isBlank(query)) {
            query = "";
        }

        Period period = findSourceDto.getPeriod();

        if (period == null) {
            throw new IllegalStateException("Period is not defined");
        }

        List<Classification> classifications = findSourceDto.getClassifications();

        if (isEmpty(classifications)) {
            classifications = Arrays.asList(Classification.values());
        }

        List<Type> types = findSourceDto.getTypes();

        if (isEmpty(types)) {
            types = Arrays.asList(Type.values());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        return findSources(period, types, classifications, query, pageable).stream()
                .map(Source::toSourceDto)
                .collect(toList());
    }

    @Override
    public SourceDto updateName(Long id, String name) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        ErrorsDto errorsDto = validationHelper.getErrors();

        if (isBlank(name)) {
            validationHelper.addError(errorsDto, NAME_FIELD, NAME_BLANK_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        Source source = getSource(id).get();
        source.setName(name);
        return saveSource(source).toSourceDto();
    }

    @Override
    public SourceDto updateDescription(Long id, String description) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(id).get();
        source.setDescription(description);
        return saveSource(source).toSourceDto();
    }

    @Override
    public void delete(Long id) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        deleteSource(id);
    }

    @Override
    public SourceDto addImage(Long id, ImageDto imageDto) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE, ROLE_CONTRIBUTOR);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(id)
                .orElseThrow(() -> new NoSuchElementException("Source with id " + id + "not found"));
        ImageDto result = imageClient.store(imageDto);
        source.getImages().add(result.getObjectId());
        return saveSource(source).toSourceDto();
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Source saveSource(Source source) {
        return sourceRepository.save(source);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private Optional<Source> getSource(Long id) {
        return sourceRepository.findById(id);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private List<Source> findSources(Period period, List<Type> types,
                                     List<Classification> classifications, String query, Pageable pageable) {
        return sourceRepository.find(period, types, classifications, query, pageable);
    }

    @HystrixCommand(commandProperties = {
            @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE")
    })
    private void deleteSource(Long id) {
        sourceRepository.deleteById(id);
    }
}
