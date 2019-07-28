package club.tempvs.library.service;

import club.tempvs.library.dto.ImageDto;

import java.util.List;

public interface ImageService {

    void delete(List<String> objectIds);

    void delete(String belongsTo, Long entityId);

    void store(ImageDto imageDto);
}
