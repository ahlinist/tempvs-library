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
import club.tempvs.library.domain.Image;
import club.tempvs.library.dto.ErrorsDto;
import club.tempvs.library.dto.ImageDto;
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
    public Source create(Source source) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles =
                Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE, ROLE_CONTRIBUTOR);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        ErrorsDto errorsDto = validationHelper.getErrors();

        if (isBlank(source.getName())) {
            validationHelper.addError(errorsDto, NAME_FIELD, NAME_BLANK_ERROR);
        }

        if (isNull(source.getClassification())) {
            validationHelper.addError(errorsDto, CLASSIFICATION_FIELD, CLASSIFICATION_MISSING_ERROR);
        }

        if (isNull(source.getPeriod())) {
            validationHelper.addError(errorsDto, PERIOD_FIELD, PERIOD_MISSING_ERROR);
        }

        if (isNull(source.getType())) {
            validationHelper.addError(errorsDto, TYPE_FIELD, TYPE_MISSING_ERROR);
        }

        validationHelper.processErrors(errorsDto);
        return saveSource(source);
    }

    public Source get(Long id) {
        return getSource(id);
    }

    @Override
    public List<Source> find(String query, Period period, List<Classification> classifications, List<Type> types, int page, int size) {
        if (isBlank(query)) {
            query = "";
        }

        if (period == null) {
            throw new IllegalStateException("Period is not defined");
        }

        if (isEmpty(classifications)) {
            classifications = Arrays.asList(Classification.values());
        }

        if (isEmpty(types)) {
            types = Arrays.asList(Type.values());
        }

        Pageable pageable = PageRequest.of(page, size, Sort.Direction.ASC, "createdDate");

        return findSources(period, types, classifications, query, pageable);
    }

    @Override
    public Source updateName(Long id, String name) {
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
        Source source = getSource(id);
        source.setName(name);
        return saveSource(source);
    }

    @Override
    public Source updateDescription(Long id, String description) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(id);
        source.setDescription(description);
        return saveSource(source);
    }

    @Override
    public void delete(Long id) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(id);
        List<String> objectIds = source.getImages()
                .stream()
                .map(Image::getObjectId)
                .collect(toList());
        imageClient.delete(objectIds);
        deleteSource(source);
    }

    @Override
    public Source addImage(Long sourceId, ImageDto imageDto) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE, ROLE_CONTRIBUTOR);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(sourceId);
        ImageDto result = imageClient.store(imageDto);
        source.getImages().add(result.toImage());
        return saveSource(source);
    }

    @Override
    public Source deleteImage(Long sourceId, String objectId) {
        User user = userHolder.getUser();
        List<Role> userRoles = user.getRoles();
        List<Role> allowedRoles = Arrays.asList(ROLE_ADMIN, ROLE_ARCHIVARIUS, ROLE_SCRIBE);

        if (Collections.disjoint(userRoles, allowedRoles)) {
            throw new ForbiddenException("Access denied");
        }

        Source source = getSource(sourceId);
        List<Image> images = source.getImages().stream()
                .filter(image -> !image.getObjectId().equals(objectId))
                .collect(toList());
        source.setImages(images);
        Source persistentSource = saveSource(source);
        imageClient.delete(objectId);
        return persistentSource;
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
    private Source getSource(Long id) {
        return sourceRepository.findById(id).get();
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
    private void deleteSource(Source source) {
        sourceRepository.delete(source);
    }
}
