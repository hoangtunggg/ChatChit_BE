package learn.spring.my_spring_project.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;

import java.time.Duration;
import java.util.Arrays;

public class CookieUtil {

    private static final String REFRESH_TOKEN_COOKIE_NAME = "rt";

    // Thêm refresh token vào cookie
//    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
//        ResponseCookie cookie = ResponseCookie
//                .from(REFRESH_TOKEN_COOKIE_NAME, refreshToken)
//                .httpOnly(true)
//                .secure(false) // bật HTTPS
//                .path("/api/auth")
//                .sameSite("Lax")
//                .maxAge(Duration.ofDays(14))
//                .build();
//        response.addHeader("Set-Cookie", cookie.toString());
//    }

    public static void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie("rt", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // ⚡ phải false khi chạy localhost (http://)
        cookie.setPath("/");     // để toàn bộ app đều đọc được
        cookie.setMaxAge((int) Duration.ofDays(14).getSeconds());
        response.addCookie(cookie);
    }

    // Lấy refresh token từ cookie
    public static String getRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;
        return Arrays.stream(request.getCookies())
                .filter(c -> REFRESH_TOKEN_COOKIE_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    // Xóa refresh token cookie (khi logout)
    public static void clearRefreshTokenCookie(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie
                .from(REFRESH_TOKEN_COOKIE_NAME, "")
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .sameSite("None")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }
}
