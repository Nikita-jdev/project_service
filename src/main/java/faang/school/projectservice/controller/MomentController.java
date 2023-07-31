package faang.school.projectservice.controller;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.dto.MomentDtoUpdate;
import faang.school.projectservice.filters.moments.FilterMomentDto;
import faang.school.projectservice.service.MomentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/moments")
public class MomentController {
    public final MomentService momentService;

    @PostMapping
    public void createMoment(@Valid MomentDto momentDto) {
        momentService.createMoment(momentDto);
    }

    @PutMapping
    public void updateMoment(@Valid MomentDtoUpdate momentDtoUpdate) {
        momentService.updateMoment(momentDtoUpdate);
    }

    @GetMapping("/filtered")
    public List<MomentDto> getFilteredMoments(FilterMomentDto filterMomentDto, Long idProject) {
        return momentService.getFilteredMoments(filterMomentDto, idProject);
    }

    @GetMapping
    public List<MomentDto> getAllMoments() {
        return momentService.getAllMoments();
    }

    @GetMapping("{id}")
    public MomentDto getMoment(@PathVariable("id") @Valid @Min(0) long momentId) {
        return momentService.getMoment(momentId);
    }
}