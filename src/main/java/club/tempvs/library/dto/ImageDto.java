package club.tempvs.library.dto;

import club.tempvs.library.domain.Image;
import lombok.Data;

@Data
public class ImageDto {

    private String objectId;
    private String imageInfo;
    private String content;
    private String fileName;

    public Image toImage() {
        return new Image(objectId, imageInfo, fileName);
    }
}
