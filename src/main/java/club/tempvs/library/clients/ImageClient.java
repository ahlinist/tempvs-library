package club.tempvs.library.clients;

import club.tempvs.library.dto.ImageDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("image")
@RequestMapping("/api/image")
public interface ImageClient {

    @PostMapping
    ImageDto store(@RequestBody ImageDto payload);

    @PostMapping("/delete")
    void delete(@RequestBody List<String> objectIds);

    @DeleteMapping("/{id}")
    void delete(@PathVariable("id") String id);
}
