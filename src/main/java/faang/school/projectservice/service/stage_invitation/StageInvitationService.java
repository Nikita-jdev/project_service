package faang.school.projectservice.service.stage_invitation;

import faang.school.projectservice.dto.stage_invitation.StageInvitationDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.mapper.stage_invitation.StageInvitationMapper;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitationStatus;
import faang.school.projectservice.repository.StageInvitationRepository;
import faang.school.projectservice.repository.StageRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StageInvitationService {
    private final StageInvitationRepository repository;
    private final StageRepository stageRepository;
    private final TeamMemberRepository TMRepository;
    private final StageInvitationMapper mapper;

    public StageInvitationDto create(StageInvitationDto invitationDto) {
        validate(invitationDto);
        StageInvitation stageInvitation = mapper.toModel(invitationDto);
        stageInvitation.setStatus(StageInvitationStatus.PENDING);
        return mapper.toDTO(repository.save(stageInvitation));
    }

    private void validate(StageInvitationDto invitationDto) {
        Stage stage = stageRepository.getById(invitationDto.getStageId());
        TMRepository.findById(invitationDto.getInvitedId());
        TMRepository.findById(invitationDto.getAuthorId());
        if (!hasStageExecutor(stage, invitationDto.getAuthorId())) {
            throw new DataValidationException("Author is not executor of stage");
        }
    }

    private boolean hasStageExecutor(Stage stage, long executorId) {
        return stage.getExecutors().stream().anyMatch(executor -> executor.getId() == executorId);
    }

    public StageInvitationDto reject(long invitationId, String message){
        StageInvitation invitation = repository.findById(invitationId);
        invitation.setStatus(StageInvitationStatus.REJECTED);
        invitation.setDescription(message);
        return mapper.toDTO(repository.save(invitation));
    }
}