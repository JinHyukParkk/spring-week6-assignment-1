package com.codesoom.assignment.application;

import com.codesoom.assignment.domain.User;
import com.codesoom.assignment.domain.UserRepository;
import com.codesoom.assignment.dto.AccountData;
import com.codesoom.assignment.errors.InvalidAccessTokenException;
import com.codesoom.assignment.errors.InvalidPasswordException;
import com.codesoom.assignment.errors.UserNotFoundException;
import com.codesoom.assignment.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthenticationServiceTest {
    final String validToken
            = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaDk";
    final String invalidToken
            = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9.ZZ3CUl0jxeLGvQ1Js5nG2Ty5qGTlqai5ubDMXZOdaD0";
    final String givenSavedEmail = "newoo4297@codesoom.com";
    final String givenUnsavedEmail = "law@codesoom.com";
    final String givenValidPassword = "1234567890";
    final String givenInvalidPassword = "12345";

    private AuthenticationService authenticationService;

    @BeforeEach
    void setUp() {
        final String secret = "12345678901234567890123456789012";

        JwtUtil jwtUtil = new JwtUtil(secret);
        UserRepository userRepository = mock(UserRepository.class);

        authenticationService = new AuthenticationService(jwtUtil, userRepository);

        given(userRepository.findByEmail(givenSavedEmail))
                .willReturn(
                        Optional.of(User.builder()
                                .id(1L)
                                .email(givenSavedEmail)
                                .name("Tester")
                                .password(givenValidPassword)
                                .build())
                );
    }

    @Test
    void loginWithValidAccountData() {
        AccountData accountData = AccountData.builder().email(givenSavedEmail).password(givenValidPassword).build();
        String accessToken = authenticationService.login(accountData);

        assertThat(accessToken).isEqualTo(validToken);
    }

    @Test
    void loginWithInvalidAccountData() {
        // With unsaved email
        AccountData accountDataWithUnsavedEmail =
                AccountData.builder()
                .email(givenUnsavedEmail)
                .password(givenValidPassword)
                .build();
        assertThatThrownBy(
                () -> authenticationService.login(accountDataWithUnsavedEmail)
        ).isInstanceOf(UserNotFoundException.class);

        // With invalid password
        AccountData accountDataWithInvalidPassword =
                AccountData.builder().email(givenSavedEmail).password(givenInvalidPassword).build();
        assertThatThrownBy(
                () -> authenticationService.login(accountDataWithInvalidPassword)
        ).isInstanceOf(InvalidPasswordException.class);
    }

    @Test
    void parseTokenWithValidToken() {
        Long userId = authenticationService.parseToken(validToken);

        assertThat(userId).isEqualTo(1L);
    }

    @Test
    void parseTokenWithInvalidToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(invalidToken)
        ).isInstanceOf(InvalidAccessTokenException.class);
    }

    @Test
    void parseTokenWithBlankToken() {
        assertThatThrownBy(
                () -> authenticationService.parseToken(null)
        ).isInstanceOf(InvalidAccessTokenException.class);

        assertThatThrownBy(
                () -> authenticationService.parseToken("")
        ).isInstanceOf(InvalidAccessTokenException.class);

        assertThatThrownBy(
                () -> authenticationService.parseToken(" ")
        ).isInstanceOf(InvalidAccessTokenException.class);
    }
}
