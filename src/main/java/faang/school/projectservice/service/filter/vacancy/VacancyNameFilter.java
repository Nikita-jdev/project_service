package faang.school.projectservice.service.filter.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.model.Vacancy;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class VacancyNameFilter implements VacancyFilter {
    @Override
    public boolean isApplicable(VacancyFilterDto vacancyFilterDto) {
        return vacancyFilterDto.getName() != null;
    }

    @Override
    public Stream<Vacancy> apply(Stream<Vacancy> vacancies, VacancyFilterDto vacancyFilterDto) {
        return vacancies.filter(vacancy -> vacancy.getName().equals(vacancyFilterDto.getName()));
    }
}