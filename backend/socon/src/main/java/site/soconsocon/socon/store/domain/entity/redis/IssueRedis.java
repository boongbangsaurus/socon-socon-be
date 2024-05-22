package site.soconsocon.socon.store.domain.entity.redis;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@ToString
@RedisHash("issue")
public class IssueRedis {
    @Id
    Integer issueId;
    String name;
    @Indexed Integer storeId;
}
