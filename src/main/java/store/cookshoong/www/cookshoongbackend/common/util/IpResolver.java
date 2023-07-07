package store.cookshoong.www.cookshoongbackend.common.util;

import javax.servlet.http.HttpServletRequest;

/**
 * request 에서 ip 값을 가져오기 위한 클래스.
 *
 * @author koesnam
 * @since 2023.07.06
 */
public class IpResolver {
    private IpResolver() {
        throw new IllegalStateException("it's util class");
    }

    /**
     * 클라이언트 ip를 가져옴.
     * LoadBalancer 나 HTTP 프록시를 통해 웹서버에 접속시 원주소와 달라짐으로 원주소를 얻기 위해서 해당 로직을 이용한다.
     *
     * @param request 클라이언트 요청
     * @return 클라이언트 IP 주소
     */
    public static String getClientIp(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");

        if (clientIp == null) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (clientIp == null) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (clientIp == null) {
            clientIp = request.getHeader("HTTP_CLIENT_IP");
        }
        if (clientIp == null) {
            clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (clientIp == null) {
            clientIp = request.getRemoteAddr();
        }

        return clientIp;
    }
}
