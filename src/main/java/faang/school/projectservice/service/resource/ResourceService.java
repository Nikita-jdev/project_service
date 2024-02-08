package faang.school.projectservice.service.resource;

import faang.school.projectservice.dto.resource.ResourceDto;
import faang.school.projectservice.exception.EntityNotFoundException;
import faang.school.projectservice.jpa.ResourceRepository;
import faang.school.projectservice.mapper.ResourceMapper;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import faang.school.projectservice.service.s3.CoverHandler;
import faang.school.projectservice.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigInteger;

@Service
@RequiredArgsConstructor
public class ResourceService {
    private final ProjectService projectService;
    private final S3Service s3Service;
    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final TeamMemberRepository teamMemberRepository;
    private final CoverHandler coverHandler;



    public ResourceDto addCoverToProject(long projectId, long userId, MultipartFile file) {
        Project project = projectService.getProjectEntityById(projectId);
        TeamMember teamMember = teamMemberRepository.findById(userId);

        coverHandler.checkCoverMemorySize(file);
        coverHandler.checkCoverSize(file);


        BigInteger newStorageSize = project.getStorageSize().add(BigInteger.valueOf(file.getSize()));
        checkStorageSizeExceeded(project.getMaxStorageSize(), newStorageSize);

        String folder = project.getId() + project.getName();
        Resource resource = s3Service.uploadFile(file, folder);
        resource.setProject(project);
        resource.setCreatedBy(teamMember);
        resource = resourceRepository.save(resource);

        project.setStorageSize(newStorageSize);
        project.setCoverImageId(resource.getKey());
        projectService.save(project);

        return resourceMapper.toDto(resource);
    }

    public InputStream downloadCover(long resourceId) {
        Resource resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Ресурс не найден"));
        return s3Service.downloadFile(resource.getKey());
    }

    public void checkStorageSizeExceeded(BigInteger maxStorageSize, BigInteger newStorageSize) {
        if (newStorageSize.compareTo(maxStorageSize) > 0) {
            throw new IllegalArgumentException("Превышен размер хранилища");
        }
    }


}