package club.tempvs.library.clients;

import club.tempvs.library.dto.ImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient("image")
public interface ImageClient {

    @PostMapping("/api/image")
    ImageDto store(@RequestBody ImageDto payload);
}
