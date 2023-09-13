package faang.school.projectservice.util;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CampaignServiceValidator {
    public void validatePublishedCampaign(Project project, TeamMember requester) {
        if (!isPublishedValid(project, requester)) {
            throw new DataValidationException("Campaign is not published");
        }
        if (!requester.getRoles().contains(TeamRole.MANAGER) && !requester.getRoles().contains(TeamRole.OWNER)) {
            throw new DataValidationException("In this project, you are neither the manager nor the owner.");
        }
    }

    private boolean isPublishedValid(Project project, TeamMember requester) {
        for (Team team : project.getTeams()) {
            List<TeamMember> teamMembers = team.getTeamMembers();
            if (teamMembers.contains(requester)) {
                return true;
            }
        }
        return false;
    }

    public void statusValidation(String status) {
        if (status != null && !status.isEmpty()) {
            if (!status.equals("ACTIVE") && !status.equals("CANCELED") && !status.equals("COMPLETED")) {
                throw new DataValidationException("Invalid status");
            }
        }
    }
}