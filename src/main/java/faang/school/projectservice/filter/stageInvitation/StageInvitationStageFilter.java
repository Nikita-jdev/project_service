package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.stage_invitation.StageInvitation;

import java.util.stream.Stream;

public class StageInvitationStageFilter implements StageInvitationFilter{
    @Override
    public boolean isApplicable(StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationFilterDto.getStageIdPattern() != null;
    }

    @Override
    public Stream<StageInvitation> apply(Stream<StageInvitation> stageInvitationStream,
                                         StageInvitationFilterDto stageInvitationFilterDto) {
        return stageInvitationStream.filter(stageInvitation ->
                stageInvitation.getStage().getStageId().equals(stageInvitationFilterDto.getStageIdPattern()));
    }
}