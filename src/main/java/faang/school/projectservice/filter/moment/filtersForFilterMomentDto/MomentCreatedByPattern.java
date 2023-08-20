package faang.school.projectservice.filter.moment.filtersForFilterMomentDto;

import faang.school.projectservice.filter.moment.FilterMomentDto;
import faang.school.projectservice.filter.moment.MomentFilter;
import faang.school.projectservice.model.Moment;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class MomentCreatedByPattern implements MomentFilter {
    @Override
    public boolean isApplicable(FilterMomentDto filterMomentDto) {
        return filterMomentDto.getCreatedByPattern() != null;
    }

    @Override
    public Stream<Moment> apply(Stream<Moment> moments, FilterMomentDto filterDto) {
        return moments.filter(moment -> Objects.equals(moment.getCreatedBy(), filterDto.getCreatedByPattern()));
    }
}