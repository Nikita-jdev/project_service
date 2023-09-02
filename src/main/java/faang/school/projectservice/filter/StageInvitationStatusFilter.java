package faang.school.projectservice.filter;

import faang.school.projectservice.dto.invitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class StageInvitationStatusFilter implements StageInvitationFilter {
    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getStatusPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> invitationsStream, StageInvitationFilterDto stageInvitationFilterDto) {
        return invitationsStream.filter(stageInvitation ->
                stageInvitation.getStatus().toString().equals(stageInvitationFilterDto.getStatusPattern()));
    }
}