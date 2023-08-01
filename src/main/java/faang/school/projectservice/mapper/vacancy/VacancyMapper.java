package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoReqUpdate;
import faang.school.projectservice.model.Vacancy;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VacancyMapper {
    @Mapping(source = "id", target = "vacancyId")
    @Mapping(source = "project.id", target = "projectId")
    VacancyDto toDto(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    Vacancy toEntity(VacancyDto vacancyDto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(VacancyDtoReqUpdate vacancyDto, @MappingTarget Vacancy vacancy);
}