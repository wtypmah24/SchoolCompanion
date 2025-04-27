package org.back.beobachtungapp.controller;

import org.back.beobachtungapp.annotation.CurrentCompanion;
import org.back.beobachtungapp.dto.request.child.ChildRequestDto;
import org.back.beobachtungapp.dto.response.child.ChildResponseDto;
import org.back.beobachtungapp.entity.child.Child;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.service.ChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("child")
public class ChildController {
    private final ChildService childService;

    @Autowired
    public ChildController(ChildService childService) {
        this.childService = childService;
    }

    @PostMapping()
    public ResponseEntity<ChildResponseDto> add(
            @RequestBody ChildRequestDto child,
            @CurrentCompanion Companion companion
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(childService.save(child, companion));
    }

    @GetMapping()
    public ResponseEntity<List<Child>> getAll(@CurrentCompanion Companion companion) {
        return ResponseEntity.status(HttpStatus.OK).body(childService.findAllByCompanion(companion));
    }
}
