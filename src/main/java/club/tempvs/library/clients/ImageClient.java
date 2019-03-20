package club.tempvs.library.clients;

import club.tempvs.library.dto.ImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient("image")
@RequestMapping("/api")
public interface ImageClient {

    @PostMapping("/image")
    ImageDto store(@RequestBody ImageDto payload);

    @PostMapping("/image/delete")
    void delete(@RequestBody List<String> objectIds);
}
