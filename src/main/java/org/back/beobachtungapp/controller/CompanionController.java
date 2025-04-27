package org.back.beobachtungapp.controller;

import org.back.beobachtungapp.dto.request.companion.CompanionRequestDto;
import org.back.beobachtungapp.dto.request.companion.LoginRequest;
import org.back.beobachtungapp.entity.companion.Companion;
import org.back.beobachtungapp.service.CompanionService;
import org.back.beobachtungapp.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class CompanionController {
    private final CompanionService companionService;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;


    @Autowired
    public CompanionController(
            CompanionService companionService,
            AuthenticationManager authenticationManager,
            TokenService tokenService
    ) {
        this.companionService = companionService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @PostMapping("/register")
    public ResponseEntity<Companion> register(@RequestBody CompanionRequestDto companion) {
        return ResponseEntity.status(HttpStatus.CREATED).body(companionService.save(companion));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.email(), loginRequest.password()
                )
        );

        String token = tokenService.generateToken(authentication);
        return ResponseEntity.ok(token);
    }
}
