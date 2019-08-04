package club.tempvs.library.service;

import static club.tempvs.library.domain.Source.*;

import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.ImageDto;

import java.util.List;

public interface SourceService {

    Source create(Source source);

    Source get(Long id);

    List<Source> getAll(List<Long> ids);

    List<Source> find(String query, Period period, List<Classification> classifications, List<Type> types, int page, int size);

    Source updateName(Long id, String name);

    Source updateDescription(Long id, String description);

    void delete(Long id);

    void addImage(Long sourceId, ImageDto imageDto);

    void deleteImage(Long sourceId, String objectId);
}
