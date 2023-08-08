package faang.school.projectservice.service.vacancy;

import faang.school.projectservice.dto.vacancy.VacancyDto;
import faang.school.projectservice.dto.vacancy.VacancyDtoGetReq;
import faang.school.projectservice.dto.vacancy.VacancyDtoUpdateReq;
import faang.school.projectservice.dto.vacancy.VacancyFilterDto;
import faang.school.projectservice.exception.vacancy.VacancyValidateException;
import faang.school.projectservice.filters.vacancy.FilterByName;
import faang.school.projectservice.filters.vacancy.FilterBySkill;
import faang.school.projectservice.filters.vacancy.VacancyFilter;
import faang.school.projectservice.mapper.vacancy.VacancyMapper;
import faang.school.projectservice.model.*;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.repository.VacancyRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.LongStream;

import static faang.school.projectservice.commonMessages.vacancy.ErrorMessagesForVacancy.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class VacancyServiceTest {
    private static final Long VACANCY_ID = 1L;
    private static final int DEFAULT_COUNT_CANDIDATES = 5;
    private static final LocalDateTime CREATED_AT = LocalDateTime.now();
    private static final LocalDateTime UPDATED_AT = LocalDateTime.now();

    @Mock
    private VacancyRepository vacancyRepository;

    @Spy
    private VacancyMapper vacancyMapper = Mappers.getMapper(VacancyMapper.class);

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private VacancyService vacancyService;

    private VacancyDto inputVacancyDto;
    private VacancyDtoUpdateReq inputVacancyDtoUpdateReq;

    private String vacancyName;
    private String vacancyDescription;
    private Long projectId;
    private Long createdBy;
    private Long updatedBy;
    private Project project;
    private TeamMember ownerVacancy;
    private TeamMember managerVacancy;
    private Vacancy savedVacancy;
    private List<List<Long>> listSkills;
    private List<Vacancy> allVacancies;

    @BeforeEach
    void setUp() {
        vacancyName = StringValuesForTesting.NAME.getValue();
        vacancyDescription = StringValuesForTesting.DESCRIPTION.getValue();
        projectId = 10L;
        createdBy = 100L;
        updatedBy = 1000L;

        project = Project.builder().id(projectId).build();
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.OWNER, TeamRole.DEVELOPER)).build();
        managerVacancy = TeamMember.builder().roles(List.of(TeamRole.MANAGER, TeamRole.DEVELOPER)).build();

        inputVacancyDto = getVacancyDtoForReqCreate();
        savedVacancy = getSavedVacancy();
        inputVacancyDtoUpdateReq = getUpdatedInputVacancyDto();
        listSkills = getListSkills();
        allVacancies = getAllSavedVacancies();
    }

    @Test
    void testCreateVacancy_WhenInputDtoIsValid() {
        VacancyDto expectedVacancyDto = getExpectedVacancyDto();
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Vacancy newVacancy = vacancyMapper.toEntity(inputVacancyDto);
        Vacancy createdVacancy = getSavedVacancy();
        Mockito.when(vacancyRepository.save(newVacancy)).thenReturn(createdVacancy);

        VacancyDto result = vacancyService.createVacancy(inputVacancyDto);

        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(createdBy);
        Mockito.verify(projectRepository, Mockito.times(1)).getProjectById(projectId);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(newVacancy);
        assertEquals(expectedVacancyDto, result);
    }

    @Test
    void testCreatedVacancy_WhenProjectNotExist_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(PROJECT_NOT_EXIST_FORMAT, projectId);
        Mockito.when(projectRepository.getProjectById(projectId))
                .thenThrow(new EntityNotFoundException(String.format("Project not found by id: %s", projectId)));

        Exception exception = assertThrows(EntityNotFoundException.class,
                () -> vacancyService.createVacancy(inputVacancyDto));

        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    void testCreatedVacancy_WhenOwnerRoleCantBeUseToCreate_ShouldThrowException() {
        ownerVacancy = TeamMember.builder().roles(List.of(TeamRole.DESIGNER, TeamRole.DEVELOPER)).build();
        String expectedMessage = MessageFormat.format(ERROR_OWNER_ROLE_FORMAT, createdBy);
        Mockito.when(projectRepository.getProjectById(projectId)).thenReturn(project);
        Mockito.when(teamMemberRepository.findById(createdBy)).thenReturn(ownerVacancy);

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.createVacancy(inputVacancyDto));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(createdBy);
    }


    @Test
    void testUpdateVacancy_WhenDataIsValid() {
        vacancyMapper.updateEntityFromDto(inputVacancyDtoUpdateReq, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        Mockito.when(vacancyRepository.save(savedVacancy)).thenReturn(savedVacancy);
        VacancyDto expectedVacancyDto = getExpectedVacancyDtoAfterUpdated();

        VacancyDto result = vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoUpdateReq);

        assertEquals(expectedVacancyDto, result);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(savedVacancy);
    }

    @Test
    void testUpdateVacancy_WhenNeedCloseVacancyAndIsPossible() {
        inputVacancyDtoUpdateReq.setStatus(VacancyStatus.CLOSED);
        vacancyMapper.updateEntityFromDto(inputVacancyDtoUpdateReq, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        Mockito.when(vacancyRepository.save(savedVacancy)).thenReturn(savedVacancy);
        VacancyDto expectedVacancyDto = getExpectedVacancyDtoAfterUpdated();
        expectedVacancyDto.setStatus(VacancyStatus.CLOSED);

        VacancyDto result = vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoUpdateReq);

        assertEquals(expectedVacancyDto, result);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
        Mockito.verify(vacancyRepository, Mockito.times(1)).save(savedVacancy);
    }

    @Test
    void testUpdateVacancy_WhenNeedCloseVacancyAndIsImpossible_ShouldThrowException() {
        savedVacancy.getCandidates().remove(0);
        inputVacancyDtoUpdateReq.setStatus(VacancyStatus.CLOSED);
        vacancyMapper.updateEntityFromDto(inputVacancyDtoUpdateReq, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(managerVacancy);
        String expectedMessage = MessageFormat.format(VACANCY_CANT_BE_CLOSED_FORMAT, VACANCY_ID, 5);

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoUpdateReq));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
    }

    @Test
    void testUpdateVacancy_WhenNotSupportedRoleUserForChanged_ShouldThrowException() {
        TeamMember someTeamMember = TeamMember.builder().roles(List.of(TeamRole.ANALYST)).build();
        vacancyMapper.updateEntityFromDto(inputVacancyDtoUpdateReq, savedVacancy);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        Mockito.when(teamMemberRepository.findById(updatedBy)).thenReturn(someTeamMember);
        String expectedMessage = MessageFormat.format(VACANCY_CANT_BE_CHANGED_FORMAT, someTeamMember.getRoles());

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.updateVacancy(VACANCY_ID, inputVacancyDtoUpdateReq));

        assertEquals(expectedMessage, exception.getMessage());
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(1)).findById(updatedBy);
    }


    @Test
    void testDeleteVacancy() {
        List<TeamMember> members = getTeamMembers(DEFAULT_COUNT_CANDIDATES);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));
        for (int i = 0; i < members.size(); i++) {
            Mockito.when(teamMemberRepository.findByUserIdAndProjectId(1L + i, projectId))
                    .thenReturn(members.get(i));
        }

        vacancyService.deleteVacancy(VACANCY_ID);

        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
        Mockito.verify(teamMemberRepository, Mockito.times(2)).deleteEntity(Mockito.any());
        Mockito.verify(teamMemberRepository, Mockito.times(5))
                .findByUserIdAndProjectId(Mockito.anyLong(), Mockito.anyLong());
        Mockito.verify(vacancyRepository, Mockito.times(1)).delete(savedVacancy);
    }

    @Test
    void testDeleteVacancy_WhenVacancyNotFoundById_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, VACANCY_ID);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.deleteVacancy(VACANCY_ID));

        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    void testGetVacancy_WhenVacancyExists() {
        VacancyDtoGetReq expectedDto = getExpectedDtoForGetReq();
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.of(savedVacancy));

        VacancyDtoGetReq resultDto = vacancyService.getVacancy(VACANCY_ID);

        assertEquals(expectedDto, resultDto);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findById(VACANCY_ID);
    }

    @Test
    void testGetVacancy_WhenVacancyNotFoundById_ShouldThrowException() {
        String expectedMessage = MessageFormat.format(VACANCY_NOT_EXIST_FORMAT, VACANCY_ID);
        Mockito.when(vacancyRepository.findById(VACANCY_ID)).thenReturn(Optional.empty());

        Exception exception = assertThrows(VacancyValidateException.class,
                () -> vacancyService.getVacancy(VACANCY_ID));

        assertEquals(expectedMessage, exception.getMessage());
    }


    @Test
    void testGetVacanciesByFilter() {
        List<VacancyFilter> vacancyFilters = getVacancyFilters();
        vacancyService = new VacancyService(
                vacancyRepository, vacancyMapper, projectRepository, teamMemberRepository, vacancyFilters);
        VacancyFilterDto vacancyFilterDto = getVacancyFilterDto();
        Mockito.when(vacancyRepository.findAll()).thenReturn(allVacancies);
        List<VacancyDto> expectedResult = getExpectedVacanciesAfterFilter();

        List<VacancyDto> result = vacancyService.getVacanciesByFilter(vacancyFilterDto);

        assertEquals(expectedResult, result);
        Mockito.verify(vacancyRepository, Mockito.times(1)).findAll();
    }


    private List<Vacancy> getAllSavedVacancies() {
        int countVacancies = listSkills.size();

        List<Vacancy> vacancies = new ArrayList<>(countVacancies);
        for (int i = 0; i < countVacancies; i++) {
            Vacancy vacancy = Vacancy.builder()
                    .id(i + 1L)
                    .name("Vacancy " + (i + 1))
                    .description("Description for vacancy " + (i + 1))
                    .requiredSkillIds(listSkills.get(i))
                    .status(VacancyStatus.OPEN)
                    .build();
            vacancies.add(vacancy);
        }
        return vacancies;
    }

    private List<VacancyFilter> getVacancyFilters() {
        FilterByName filter1 = new FilterByName();
        FilterBySkill filter2 = new FilterBySkill();
        return List.of(filter1, filter2);
    }

    private VacancyFilterDto getVacancyFilterDto() {
        String namePattern = "2";
        List<Long> needSkill = List.of(2L, 3L);
        return VacancyFilterDto.builder()
                .namePattern(namePattern)
                .skillsPattern(needSkill)
                .build();
    }

    private List<VacancyDto> getExpectedVacanciesAfterFilter() {
        VacancyDto vacancyDto = VacancyDto.builder()
                .vacancyId(2L)
                .name("Vacancy 2")
                .description("Description for vacancy 2")
                .build();

        return List.of(vacancyDto);
    }

    private List<List<Long>> getListSkills() {
        List<Long> skills1 = List.of(1L, 2L, 3L);
        List<Long> skills2 = List.of(3L, 4L, 5L, 2L);
        List<Long> skills3 = List.of(6L, 7L, 8L, 2L);

        return List.of(
                skills1,
                skills2,
                skills3
        );
    }

    private List<TeamMember> getTeamMembers(int count) {
        List<TeamMember> teamMembers = new ArrayList<>(count);
        for (int i = 1; i < count + 1; i++) {
            TeamMember teamMember = TeamMember.builder()
                    .userId((long) i)
                    .build();

            if (i == 1 || i == 2) {
                teamMember.setRoles(List.of(TeamRole.INTERN, TeamRole.ANALYST));
            } else {
                teamMember.setRoles(List.of(TeamRole.ANALYST, TeamRole.DESIGNER));
            }

            teamMembers.add(teamMember);
        }
        return teamMembers;
    }

    private List<Candidate> getCandidates(int count) {
        List<Candidate> candidates = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            candidates.add(Candidate.builder().id(i + 1L).userId(i + 1L).build());
        }
        return candidates;
    }

    private VacancyDto getVacancyDtoForReqCreate() {
        return VacancyDto.builder()
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy).build();
    }

    private VacancyDtoGetReq getExpectedDtoForGetReq() {
        List<Long> candidateId = LongStream.rangeClosed(1, DEFAULT_COUNT_CANDIDATES).boxed().toList();
        return VacancyDtoGetReq.builder()
                .vacancyId(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(project.getId())
                .candidatesId(candidateId)
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .status(VacancyStatus.OPEN)
                .salary(500.0)
                .workSchedule(WorkSchedule.ON_CALL)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .build();
    }

    private VacancyDto getExpectedVacancyDto() {
        return VacancyDto.builder()
                .vacancyId(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .projectId(projectId)
                .createdBy(createdBy)
                .status(VacancyStatus.OPEN)
                .build();
    }

    private Vacancy getSavedVacancy() {
        return Vacancy.builder()
                .id(VACANCY_ID)
                .name(vacancyName)
                .description(vacancyDescription)
                .project(project)
                .candidates(getCandidates(DEFAULT_COUNT_CANDIDATES))
                .createdAt(CREATED_AT)
                .updatedAt(UPDATED_AT)
                .createdBy(createdBy)
                .updatedBy(updatedBy)
                .salary(500.0)
                .workSchedule(WorkSchedule.ON_CALL)
                .requiredSkillIds(List.of(1L, 2L, 3L))
                .status(VacancyStatus.OPEN)
                .build();
    }

    private VacancyDto getExpectedVacancyDtoAfterUpdated() {
        Vacancy expectedVacancy = getSavedVacancy();
        expectedVacancy.setName(StringValuesForTesting.UPDATED_NAME.getValue());
        expectedVacancy.setDescription(StringValuesForTesting.UPDATED_DESCRIPTION.getValue());
        expectedVacancy.setUpdatedBy(updatedBy);
        return vacancyMapper.toDto(expectedVacancy);
    }

    private VacancyDtoUpdateReq getUpdatedInputVacancyDto() {
        String updatedVacancyName = StringValuesForTesting.UPDATED_NAME.getValue();
        String updatedVacancyDescription = StringValuesForTesting.UPDATED_DESCRIPTION.getValue();

        return VacancyDtoUpdateReq.builder()
                .vacancyId(VACANCY_ID)
                .name(updatedVacancyName)
                .description(updatedVacancyDescription)
                .updatedBy(updatedBy)
                .status(VacancyStatus.OPEN)
                .build();
    }
}

enum StringValuesForTesting {
    NAME("Test vacancy name"),
    UPDATED_NAME("Updated vacancy name"),
    DESCRIPTION("Some description of vacancy"),
    UPDATED_DESCRIPTION("Description was updated");

    private final String value;

    StringValuesForTesting(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}