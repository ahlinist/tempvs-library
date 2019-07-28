package club.tempvs.library.controller;

import club.tempvs.library.domain.Source;
import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.ImageDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/source")
@RequiredArgsConstructor
@Validated
public class SourceController {

    private static final int MAX_SIZE_VALUE = 40;

    private final SourceService sourceService;

    @PostMapping
    public Source create(@RequestBody SourceDto sourceDto) {
        return sourceService.create(sourceDto.toSource());
    }

    @GetMapping("/{id}")
    public Source get(@PathVariable Long id) {
        return sourceService.get(id);
    }

    @GetMapping
    public List<SourceDto> find(
            @RequestParam FindSourceDto q,
            @RequestParam int page,
            @Max(MAX_SIZE_VALUE) @RequestParam int size) {

        return sourceService.find(q.getQuery(), q.getPeriod(), q.getClassifications(), q.getTypes(), page, size).stream()
                .map(Source::toSourceDto)
                .collect(toList());
    }

    @PatchMapping("/{id}/name")
    public void updateName(@PathVariable Long id, @RequestBody Map<String, String> payload) {

        sourceService.updateName(id, payload.get("name"));
    }

    @PatchMapping("/{id}/description")
    public void updateDescription(@PathVariable Long id, @RequestBody Map<String, String> payload) {

        sourceService.updateDescription(id, payload.get("description"));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {

        sourceService.delete(id);
    }

    @PostMapping("/{sourceId}/images")
    public void addImage(@PathVariable Long sourceId, @RequestBody ImageDto imageDto) {
        sourceService.addImage(sourceId, imageDto);
    }

    @DeleteMapping("/{sourceId}/images/{objectId}")
    public void deleteImage(@PathVariable Long sourceId, @PathVariable String objectId) {
        sourceService.deleteImage(sourceId, objectId);
    }
}
