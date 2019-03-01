package club.tempvs.library.controller;

import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/source")
@RequiredArgsConstructor
public class SourceController {

    private final SourceService sourceService;

    @PostMapping
    public SourceDto create(@RequestBody SourceDto sourceDto) {
        return sourceService.create(sourceDto);
    }

    @GetMapping("/{id}")
    public SourceDto get(@PathVariable Long id) {
        return sourceService.get(id);
    }
}
