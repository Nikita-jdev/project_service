package faang.school.projectservice.mapper.invitationMaper;

import faang.school.projectservice.dto.invitation.DtoStageInvitation;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StageInvitationMapper {
    StageInvitationMapper INSTANCE = Mappers.getMapper(StageInvitationMapper.class);

    @Mapping(source = "idAuthor", target = "author.id")
    @Mapping(source = "idInvited", target = "invited.id")
    @Mapping(source = "stage.stageId", target = "stage.stageId")
    StageInvitation toStageInvitation(DtoStageInvitation dto);

    @Mapping(source = "author.id", target = "idAuthor")
    @Mapping(source = "invited.id", target = "idInvited")
    DtoStageInvitation toDto(StageInvitation stageInvitation);
}