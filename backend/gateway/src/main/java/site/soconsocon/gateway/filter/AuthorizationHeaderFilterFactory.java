package site.soconsocon.gateway.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import site.soconsocon.gateway.util.JwtUtil;

import java.util.Map;
import java.util.Set;

import static java.nio.charset.StandardCharsets.*;

@Component
@Slf4j
public class AuthorizationHeaderFilterFactory extends AbstractGatewayFilterFactory<AuthorizationHeaderFilterFactory.Config> {

    @Autowired
    private JwtUtil jwtUtil;
    Environment env;

    public AuthorizationHeaderFilterFactory(Environment env) {
        super(Config.class);
        this.env = env;
    }

    public static class Config {
        // application.yml 파일에서 지정한 filer의 Argument값을 받는 부분
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("요청한 uri : " + request.getURI());

            // Authorization 헤더가 없다면 에러
            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "No authorization header", HttpStatus.UNAUTHORIZED);
            }

            HttpHeaders headers = request.getHeaders();
            Set<String> keys = headers.keySet();
            log.info(">>>");
            keys.stream().forEach(v -> {
                log.info(v + "=" + request.getHeaders().get(v));
            });
            log.info("<<<");

            String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String jwt = authorizationHeader.replace("Bearer ", "");
            log.info(jwt);

            if (!jwtUtil.validateToken(jwt)) {
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }

            log.info("JWT valid");
            Map<String, Object> userInfo = jwtUtil.getUserParseInfo(jwt);

            // memberId를 가져와서 HTTP 헤더에 추가
            addAuthorizationHeaders(exchange.getRequest(), userInfo);

            return chain.filter(exchange);
        };
    }

    // 성공적으로 검증이 되었기 때문에 인증된 헤더로 요청을 변경해준다. 서비스는 해당 헤더에서 아이디를 가져와 사용한다.
    private void addAuthorizationHeaders(ServerHttpRequest request, Map<String, Object> userInfo) {
        request.mutate()
                .header("X-Authorization-Id", userInfo.get("memberId").toString())
                .header("X-Authorization-Role", userInfo.get("role").toString())
                .build();
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);

        byte[] bytes = "The requested token is invalid.".getBytes(UTF_8);
        DataBuffer buffer = exchange.getResponse().bufferFactory().wrap(bytes);
        return response.writeWith(Flux.just(buffer));

//        return response.setComplete();
    }

    private boolean isJwtValid(String jwt) {
        log.info("[JwtTokenProvider] validateToken, 토큰 유효성 체크");
        boolean returnValue = true;

        String subject = null;

        try {
            subject = Jwts
                    .parserBuilder()
                    .setSigningKey(env.getProperty("jwt.secret"))
                    .build()
                    .parseClaimsJws(jwt).getBody().getSubject();
            log.info(subject);
        } catch (Exception ex) {
            log.info(subject);
            log.info("[JwtTokenProvider] validateToken, 토큰 유효성 체크 예외 발생");
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }
        return returnValue;

    }

}
