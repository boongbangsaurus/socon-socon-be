package site.soconsocon.socon.store.repository.redis;

import org.springframework.data.repository.CrudRepository;
import site.soconsocon.socon.store.domain.entity.redis.IssueRedis;

import java.util.List;

public interface IssueRedisRepository extends CrudRepository<IssueRedis, Integer> {
    List<IssueRedis> findByStoreId(Integer storeId);
}