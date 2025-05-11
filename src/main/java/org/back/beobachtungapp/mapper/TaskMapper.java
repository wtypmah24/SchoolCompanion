package org.back.beobachtungapp.mapper;

import java.util.List;
import org.back.beobachtungapp.dto.request.task.TaskRequestDto;
import org.back.beobachtungapp.dto.request.task.TaskUpdateDto;
import org.back.beobachtungapp.dto.response.task.TaskResponseDto;
import org.back.beobachtungapp.entity.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TaskMapper {
  Task taskRequestDtoToTask(TaskRequestDto taskRequestDto);

  TaskResponseDto taskToTaskResponseDto(Task task);

  List<TaskResponseDto> taskToTaskResponseDtoList(List<Task> tasks);

  void updateTask(TaskUpdateDto dto, @MappingTarget Task task);
}
