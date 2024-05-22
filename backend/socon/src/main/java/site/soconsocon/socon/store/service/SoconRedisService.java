package site.soconsocon.socon.store.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.GeoOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import site.soconsocon.socon.global.GeoUtils;
import site.soconsocon.socon.search.domain.dto.request.StoreNearMe;
import site.soconsocon.socon.sogon.domain.dto.feign.FcmMessage;
import site.soconsocon.socon.sogon.feign.NotificationFeignClient;
import site.soconsocon.socon.store.domain.entity.jpa.Socon;
import site.soconsocon.socon.store.domain.entity.redis.SoconRedis;
import site.soconsocon.socon.store.repository.redis.SoconRedisRepository;


import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class SoconRedisService {
    private final SoconRedisRepository soconRedisRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final NotificationFeignClient notificationFeignClient;

    public void saveSocon(Socon socon) {
        // Socon 엔티티를 SoconRedis 객체로 변환
        SoconRedis soconRedis = SoconRedis.builder()
                .soconId(socon.getId())
                .memberId(socon.getMemberId())
                // 임의의 lat, lng 값을 설정해야 함. 실제로는 Socon 엔티티에서 이 정보를 가져와야 함
                .lat(socon.getIssue().getItem().getStore().getLat())
                .lng(socon.getIssue().getItem().getStore().getLng())
                // 만료 시간 설정
                .expiration(ChronoUnit.SECONDS.between(socon.getPurchasedAt(), socon.getExpiredAt()))
                .build();

        // Redis에 SoconRedis 객체 저장
        soconRedisRepository.save(soconRedis);

        // 위치 정보 저장 (지리 공간 데이터)
        // "soconLocations"는 Redis 내의 지리 공간 데이터를 저장하는 키입니다.
        // 실제 lat, lng 값을 사용해야 함
        redisTemplate.opsForGeo().add("soconLocations", new Point(soconRedis.getLng(), soconRedis.getLat()), String.valueOf(soconRedis.getSoconId()));
    }

    public void findAndNotify(StoreNearMe storeNearMe, int memberId) {
        List<SoconRedis> soconsByMember = soconRedisRepository.findByMemberId(memberId);

        GeoOperations<String, String> geoOperations = redisTemplate.opsForGeo();
        List<Integer> soconIds = soconsByMember.stream()
                .map(SoconRedis::getSoconId)
                .collect(Collectors.toList());

        // 모든 SoconRedis 엔티티의 위치 조회
        List<Point> points = geoOperations.position("soconLocations", soconIds.toArray(new String[0]));

        // 30미터 범위 내의 SoconRedis 엔티티 필터링
        List<SoconRedis> soconRedisList = IntStream.range(0, points.size())
                .filter(i -> points.get(i) != null &&
                        GeoUtils.distance(storeNearMe.getLat(), storeNearMe.getLng(), points.get(i).getY(), points.get(i).getX()) <= 30)
                .mapToObj(soconsByMember::get)
                .toList();

        notificationFeignClient.sendMessageMember(
                FcmMessage.builder()
                        .title("내 주변 소콘 알림")
                        .body("30미터 범위에 사용가능한 소콘이 "+soconRedisList.size()+"개 있어요!")
                .build());

    }
}
