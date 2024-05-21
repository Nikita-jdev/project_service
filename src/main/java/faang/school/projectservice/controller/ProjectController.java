package faang.school.projectservice.controller;

import faang.school.projectservice.service.ProjectService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/projects")
@RequiredArgsConstructor
@Validated
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping("/{projectId}/cover")
    public ResponseEntity<String> uploadCover(@PathVariable @Min(value = 1, message = "Project ID must be more than 0") Long projectId,
                                              @RequestParam("cover") @NotNull MultipartFile multipartFile) {
        String fileName = multipartFile.getOriginalFilename();
        projectService.uploadFile(multipartFile, projectId, "cover");
        return ResponseEntity.ok("Image uploaded: " + fileName);
    }
}
