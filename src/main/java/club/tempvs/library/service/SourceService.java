package club.tempvs.library.service;

import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.dto.SourceDto;

import java.util.List;

public interface SourceService {

    SourceDto create(SourceDto sourceDto);

    SourceDto get(Long id);

    List<SourceDto> find(FindSourceDto findSourceDto, int page, int size);

    SourceDto updateName(Long id, String name);

    SourceDto updateDescription(Long id, String description);

    void delete(Long id);

    SourceDto addImage(Long id, ImageDto imageDto);
}
