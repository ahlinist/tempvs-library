package club.tempvs.library.controller;

import club.tempvs.library.dto.FindSourceDto;
import club.tempvs.library.dto.SourceDto;
import club.tempvs.library.service.SourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping
    public List<SourceDto> find(@RequestParam FindSourceDto q) {
        return sourceService.find(q);
    }
}
