package faang.school.projectservice.InternshipController;

import faang.school.projectservice.InternshipService.InternshipService;
import faang.school.projectservice.dto.client.InternshipDto;
import faang.school.projectservice.dto.client.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import faang.school.projectservice.validator.InternshipValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class InternshipController {
    private final InternshipService internshipService;

    public InternshipDto saveNewInternship(InternshipDto internshipDto) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.saveNewInternship(internshipDto);
    }

    public InternshipDto updateInternship(Internship oldInternship, InternshipDto internshipDto, long id) {
        InternshipValidator.validateControllerInternship(internshipDto);
        return internshipService.updateInternship(internshipDto, id);
    }

    public List<InternshipDto> findInternshipsWithFilter(long projectId, InternshipFilterDto filterDto) {
        return internshipService.findInternshipsWithFilter(projectId, filterDto);
    }
}
