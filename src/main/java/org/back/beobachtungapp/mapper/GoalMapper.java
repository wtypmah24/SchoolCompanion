package org.back.beobachtungapp.mapper;

import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.entity.child.Goal;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface GoalMapper {
    Goal goalRequestDtoToGoal(GoalRequestDto goalRequestDto);
}
