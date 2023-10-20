package com.onetwo.userservice.adapter.in.web.user.api;

import com.onetwo.userservice.adapter.in.web.token.mapper.TokenDtoMapper;
import com.onetwo.userservice.adapter.in.web.user.mapper.UserDtoMapper;
import com.onetwo.userservice.adapter.in.web.user.request.LoginUserRequest;
import com.onetwo.userservice.adapter.in.web.user.response.LogoutResponse;
import com.onetwo.userservice.adapter.in.web.user.response.TokenResponse;
import com.onetwo.userservice.application.port.in.user.command.LoginUserCommand;
import com.onetwo.userservice.application.port.in.user.command.LogoutUserCommand;
import com.onetwo.userservice.application.port.in.user.usecase.LoginUseCase;
import com.onetwo.userservice.application.port.in.user.usecase.LogoutUseCase;
import com.onetwo.userservice.application.service.response.LogoutResponseDto;
import com.onetwo.userservice.application.service.response.TokenResponseDto;
import com.onetwo.userservice.common.GlobalUrl;
import com.onetwo.userservice.domain.user.MyUserDetail;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final LoginUseCase loginUseCase;
    private final LogoutUseCase logoutUseCase;
    private final UserDtoMapper userDtoMapper;
    private final TokenDtoMapper tokenDtoMapper;

    @PostMapping(GlobalUrl.USER_LOGIN)
    public ResponseEntity<TokenResponse> loginUser(@RequestBody @Valid LoginUserRequest loginUserRequest) {
        LoginUserCommand loginUserCommand = userDtoMapper.loginRequestToCommand(loginUserRequest);
        TokenResponseDto tokenResponseDto = loginUseCase.loginUser(loginUserCommand);
        return ResponseEntity.ok().body(tokenDtoMapper.tokenDtoToTokenResponse(tokenResponseDto));
    }

    @DeleteMapping(GlobalUrl.USER_LOGIN)
    public ResponseEntity<LogoutResponse> logoutUser(@AuthenticationPrincipal MyUserDetail userDetails) {
        String userId = userDetails.getUsername();
        LogoutUserCommand logoutUserCommand = userDtoMapper.logoutRequestToCommand(userId);
        LogoutResponseDto logoutResponseDto = logoutUseCase.logoutUser(logoutUserCommand);
        return ResponseEntity.ok().body(userDtoMapper.dtoToLogoutResponse(logoutResponseDto));
    }
}
