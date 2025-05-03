package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.child.GoalRequestDto;
import org.back.beobachtungapp.dto.response.child.GoalResponseDto;
import org.back.beobachtungapp.entity.child.Goal;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface GoalMapper {

  Goal goalRequestDtoToGoal(GoalRequestDto goalRequestDto);

  GoalResponseDto goalToGoalResponseDto(Goal goal);

  void updateGoalFromDto(GoalRequestDto dto, @MappingTarget Goal goal);

  List<GoalResponseDto> goalToGoalResponseDtoList(List<Goal> goals);
}
