package com.onetwo.userservice.application.port.in.token.usecase;

import com.onetwo.userservice.application.port.in.token.command.ReissueTokenCommand;
import com.onetwo.userservice.application.port.in.user.command.LoginUserCommand;
import com.onetwo.userservice.application.port.in.user.command.RegisterUserCommand;
import com.onetwo.userservice.application.port.in.user.usecase.LoginUseCase;
import com.onetwo.userservice.application.port.out.token.CreateRefreshTokenPort;
import com.onetwo.userservice.application.port.out.user.CreateUserPort;
import com.onetwo.userservice.application.service.response.ReissuedTokenResponseDto;
import com.onetwo.userservice.application.service.response.TokenResponseDto;
import com.onetwo.userservice.common.exceptions.TokenValidationException;
import com.onetwo.userservice.domain.token.RefreshToken;
import com.onetwo.userservice.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@SpringBootTest
@Transactional
class ReissueAccessTokenUseCaseBootTest {

    @Autowired
    private ReissueAccessTokenUseCase reissueAccessTokenUseCase;

    @Autowired
    private CreateUserPort createUserPort;

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CreateRefreshTokenPort createRefreshTokenPort;

    private final Long uuid = 1L;
    private final String userId = "12OneTwo12";
    private final String password = "password";
    private final Instant birth = Instant.now();
    private final String nickname = "newNickname";
    private final String name = "tester";
    private final String email = "onetwo12@onetwo.com";
    private final String phoneNumber = "01098006069";
    private final String accessToken = "access-token";
    private final String refreshToken = "refresh-token";

    @Test
    @DisplayName("[통합][Use Case] Access Token 재발급 - 성공 테스트")
    void reissueAccessTokenByRefreshTokenUseCaseSuccessTest() {
        //given
        RegisterUserCommand registerUserCommand = new RegisterUserCommand(userId, password, birth, nickname, name, email, phoneNumber);
        User user = User.createNewUserByCommand(registerUserCommand, passwordEncoder.encode(password));

        createUserPort.registerNewUser(user);

        LoginUserCommand loginUserCommand = new LoginUserCommand(userId, password);
        TokenResponseDto tokenResponseDto = loginUseCase.loginUser(loginUserCommand);
        ReissueTokenCommand reissueTokenCommand = new ReissueTokenCommand(tokenResponseDto.accessToken(), tokenResponseDto.refreshToken());

        //when
        ReissuedTokenResponseDto result = reissueAccessTokenUseCase.reissueAccessTokenByRefreshToken(reissueTokenCommand);

        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.accessToken());
    }

    @Test
    @DisplayName("[통합][Use Case] Access Token 재발급 Refresh Token does not exist - 실패 테스트")
    void reissueAccessTokenByRefreshTokenUseCaseRefreshTokenDoesNotExistTest() {
        //given
        ReissueTokenCommand reissueTokenCommand = new ReissueTokenCommand(accessToken, refreshToken);

        //when then
        Assertions.assertThrows(TokenValidationException.class, () -> reissueAccessTokenUseCase.reissueAccessTokenByRefreshToken(reissueTokenCommand));
    }

    @Test
    @DisplayName("[통합][Use Case] Access Token 재발급 token validation fail - 실패 테스트")
    void reissueAccessTokenByRefreshTokenUseCaseTokenValidationFailTest() {
        //given
        ReissueTokenCommand reissueTokenCommand = new ReissueTokenCommand(accessToken, refreshToken);
        RefreshToken refreshTokenDomain = RefreshToken.createRefreshToken(uuid, accessToken, refreshToken);

        createRefreshTokenPort.saveRefreshToken(refreshTokenDomain);

        //when then
        Assertions.assertThrows(TokenValidationException.class, () -> reissueAccessTokenUseCase.reissueAccessTokenByRefreshToken(reissueTokenCommand));
    }
}