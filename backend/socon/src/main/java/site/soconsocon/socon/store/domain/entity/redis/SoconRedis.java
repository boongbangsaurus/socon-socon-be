package site.soconsocon.socon.store.domain.entity.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Builder
@ToString
@RedisHash("socon")
public class SoconRedis {
    @Id
    private Integer soconId;

    private Integer memberId;
    private Double lat;  // 위도
    private Double lng;  // 경도

    @TimeToLive
    private Long expiration;
}
