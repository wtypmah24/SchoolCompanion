package org.back.beobachtungapp.dto.openai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RunResponse(String id, String status) {}
