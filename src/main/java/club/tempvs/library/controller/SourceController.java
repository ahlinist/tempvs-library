package club.tempvs.library.controller;

import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import java.util.List;

@RestController
@RequestMapping("/api/source")
@RequiredArgsConstructor
@Validated
public class SourceController {

    private static final int DEFAULT_SIZE_VALUE = 40;

    private final SourceService sourceService;

    @PostMapping
    public SourceDto create(@RequestBody SourceDto sourceDto) {
        return sourceService.create(sourceDto);
    }

    @GetMapping("/{id}")
    public SourceDto get(@PathVariable Long id) {
        return sourceService.get(id);
    }

    @GetMapping
    public List<SourceDto> find(
            @RequestParam FindSourceDto q,
            @RequestParam int page,
            @Max(DEFAULT_SIZE_VALUE) @RequestParam int size) {

        return sourceService.find(q, page, size);
    }
}
