package com.pd.gilgeorigoreuda.login.controller;

import com.pd.gilgeorigoreuda.login.domain.MemberAccessRefreshToken;
import com.pd.gilgeorigoreuda.login.dto.request.LoginRequest;
import com.pd.gilgeorigoreuda.login.dto.response.AccessTokenResponse;
import com.pd.gilgeorigoreuda.login.service.LoginService;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/login/{provider}")
    public ResponseEntity<AccessTokenResponse> login(
            @PathVariable final String provider,
            @RequestBody final LoginRequest loginRequest
    ) {
        MemberAccessRefreshToken memberAccessRefreshToken = loginService.login(provider, loginRequest.getCode());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AccessTokenResponse.of(memberAccessRefreshToken.getAccessToken()));
    }

    @PostMapping("/token")
    public ResponseEntity<AccessTokenResponse> extendLogin(
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        final String renewalAccessToken = loginService.renewalAccessToken(authorizationHeader);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(AccessTokenResponse.of(renewalAccessToken));
    }

    @DeleteMapping("/logout")
    // TODO: 권한 검증
    public ResponseEntity<Void> logout(
            // TODO: 로그인 유저 정보
            @RequestHeader("Authorization") final String authorizationHeader
    ) {
        loginService.deleteMemberToken(authorizationHeader);

        return ResponseEntity
                .noContent()
                .build();
    }

}
