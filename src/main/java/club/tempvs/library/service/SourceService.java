package club.tempvs.library.service;

import club.tempvs.library.dto.SourceDto;

public interface SourceService {

    SourceDto create(SourceDto sourceDto);

    SourceDto get(Long id);
}
