package faang.school.projectservice.filter.stageInvitation;

import faang.school.projectservice.dto.stageInvitation.StageInvitationFilterDto;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage_invitation.StageInvitation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class StageInvitationInvitedFilterTest {
    @InjectMocks
    private StageInvitationInvitedFilter invitedFilter;
    StageInvitationFilterDto filterDto;
    StageInvitation invitation1;
    StageInvitation invitation2;
    StageInvitation invitation3;

    @BeforeEach
    void setUp() {
        filterDto = StageInvitationFilterDto
                .builder()
                .invitedIdPattern(1L)
                .build();

        invitation1 = StageInvitation
                .builder()
                .invited(TeamMember.builder().id(2L).build())
                .build();

        invitation2 = StageInvitation
                .builder()
                .invited(TeamMember.builder().id(1L).build())
                .build();

        invitation3 = StageInvitation
                .builder()
                .invited(TeamMember.builder().id(1L).build())
                .build();
    }

    @Test
    void testIsApplicable() {
        boolean isApplicable = invitedFilter.isApplicable(filterDto);
        assertTrue(isApplicable);
    }

    @Test
    void testApply() {
        Stream<StageInvitation> invitation = Stream.of(invitation1, invitation2);
        List<StageInvitation> apply = invitedFilter.apply(invitation, filterDto).toList();
        List<StageInvitation> expected = List.of(invitation2);
        Assertions.assertEquals(expected, apply);
    }

    @Test
    void testApplyAndNewInvitation() {
        Stream<StageInvitation> invitation = Stream.of(invitation1, invitation2, invitation3);
        List<StageInvitation> apply = invitedFilter.apply(invitation, filterDto).toList();
        List<StageInvitation> expected = List.of(invitation2, invitation3);
        Assertions.assertEquals(expected, apply);
    }
}