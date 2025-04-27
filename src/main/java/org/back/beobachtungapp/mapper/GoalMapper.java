package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.entity.child.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface GoalMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "child", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Goal goalRequestDtoToGoal(GoalRequestDto goalRequestDto);
}
