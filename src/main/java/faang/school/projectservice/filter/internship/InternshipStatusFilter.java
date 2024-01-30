package faang.school.projectservice.filter.internship;

import faang.school.projectservice.dto.internship.InternshipFilterDto;
import faang.school.projectservice.model.Internship;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.stream.Stream;
@Component
public class InternshipStatusFilter implements InternshipFilter{
    @Override
    public boolean isApplicable(InternshipFilterDto internshipFilterDto) {
        return internshipFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Internship> apply(Stream<Internship> internshipStream, InternshipFilterDto internshipFilterDto) {
        return internshipStream.filter(internship -> internship.getStatus().equals(internshipFilterDto.getStatus()));
    }
}
