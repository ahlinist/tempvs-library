package club.tempvs.library.dto;

import lombok.Data;

@Data
public class ImageDto {

    private String objectId;
    private String imageInfo;
    private Long entityId;
    private String belongsTo;
    private String content;
    private String fileName;
}
