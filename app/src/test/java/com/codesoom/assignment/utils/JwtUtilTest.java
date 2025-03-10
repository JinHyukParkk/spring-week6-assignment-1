package com.codesoom.assignment.utils;

import com.codesoom.assignment.errors.InvalidTokenException;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@DisplayName("JwtUtil 클래스")
class JwtUtilTest {

    private static final String VALID_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjF9." +
            "neCsyNLzy3lQ4o2yliotWT06FwSGZagaHpKdAkjnGGw";

    private static final String INVALID_TOKEN = VALID_TOKEN + "ABCDE";

    private static final Long USER_ID = 1L;

    @Autowired
    private JwtUtil jwtUtil;

    @Nested
    @DisplayName("encode() 메소드는")
    class Describe_encode {

        @Test
        @DisplayName("token을 리턴합니다.")
        void it_return_token() {
            String token = jwtUtil.encode(USER_ID);

            assertThat(token).isEqualTo(VALID_TOKEN);
        }
    }

    @Nested
    @DisplayName("decode() 메소드는")
    class Describe_decode {

        @Test
        @DisplayName("userId를 리턴합니다.")
        void it_return_userId() {
            Claims claims = jwtUtil.decode(VALID_TOKEN);

            assertThat(claims.get("userId", Long.class)).isEqualTo(USER_ID);
        }

        @Nested
        @DisplayName("유효하지 않는 token이 주어진다면")
        class Context_with_invalid_token {

            @Test
            @DisplayName("토큰이 유효하지 않다는 예외를 던진다.")
            void it_throw_InvalidTokenException() {
                assertThatThrownBy(() -> jwtUtil.decode(INVALID_TOKEN))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }

        @Nested
        @DisplayName("null 또는 빈값이 주어진다면")
        class Context_with_null {

            @DisplayName("토큰이 유효하지 않다는 예외를 던진다.")
            @ParameterizedTest(name = "{displayName} ({argumentsWithNames})")
            @NullAndEmptySource
            void it_throw_InvalidTokenException(String input) {
                assertThatThrownBy(() -> jwtUtil.decode(input))
                        .isInstanceOf(InvalidTokenException.class);
            }
        }
    }
}
