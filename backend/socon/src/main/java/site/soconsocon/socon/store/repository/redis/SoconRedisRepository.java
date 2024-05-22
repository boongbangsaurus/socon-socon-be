package site.soconsocon.socon.store.repository.redis;

import org.springframework.data.repository.CrudRepository;
import site.soconsocon.socon.store.domain.entity.redis.SoconRedis;

import java.util.List;

public interface SoconRedisRepository  extends CrudRepository<SoconRedis, Integer> {
    List<SoconRedis> findByMemberId(Integer memberId);
}
