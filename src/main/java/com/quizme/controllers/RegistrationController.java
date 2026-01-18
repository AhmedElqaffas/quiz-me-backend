package com.quizme.controllers;

import com.quizme.dto.RegisterCredentialsRequestDto;
import com.quizme.mappers.ResultToResponseEntityMapper;
import com.quizme.services.RegistrationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;
    @Autowired
    private ResultToResponseEntityMapper responseMapper;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterCredentialsRequestDto body, HttpServletRequest request) {
        var result = registrationService.register(body);
        return responseMapper.map(result, request.getRequestURI());
    }
}
