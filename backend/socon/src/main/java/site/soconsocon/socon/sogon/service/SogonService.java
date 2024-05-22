package site.soconsocon.socon.sogon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soconsocon.socon.global.domain.ErrorCode;
import site.soconsocon.socon.global.exception.SoconException;
import site.soconsocon.socon.sogon.domain.dto.feign.FcmMessage;
import site.soconsocon.socon.sogon.domain.dto.request.AddCommentRequest;
import site.soconsocon.socon.sogon.domain.dto.request.AddSogonRequest;
import site.soconsocon.socon.sogon.domain.dto.response.*;
import site.soconsocon.socon.sogon.domain.entity.jpa.Comment;
import site.soconsocon.socon.sogon.domain.entity.jpa.Sogon;
import site.soconsocon.socon.sogon.exception.SogonErrorCode;
import site.soconsocon.socon.sogon.exception.SogonException;
import site.soconsocon.socon.sogon.feign.NotificationFeignClient;
import site.soconsocon.socon.sogon.repository.jpa.CommentRepository;
import site.soconsocon.socon.sogon.repository.jpa.SogonRepository;
import site.soconsocon.socon.store.domain.entity.feign.Member;
import site.soconsocon.socon.store.domain.entity.jpa.Socon;
import site.soconsocon.socon.store.domain.entity.jpa.SoconStatus;
import site.soconsocon.socon.store.exception.StoreErrorCode;
import site.soconsocon.socon.store.exception.StoreException;
import site.soconsocon.socon.store.feign.FeignServiceClient;
import site.soconsocon.socon.store.repository.jpa.SoconRepository;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class SogonService {

    private final SoconRepository soconRepository;
    private final SogonRepository sogonRepository;
    private final CommentRepository commentRepository;
    private final FeignServiceClient feignServiceClient;
    private final NotificationFeignClient notificationFeignClient;



    // 소곤 작성
    public void addSogon(AddSogonRequest request, int memberId) {

        // 유효한 소콘인지 체크
        Socon socon = soconRepository.findById(request.getSoconId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));
        if (!Objects.equals(socon.getStatus(), SoconStatus.unused) || // 사용가능한 소콘이 아닐 경우
                socon.getExpiredAt().isBefore(LocalDateTime.now()) // 만료된 소콘일 경우
        ) {
            throw new StoreException(StoreErrorCode.INVALID_SOCON);
        }

        if (!Objects.equals(socon.getMemberId(), memberId)) {
            // 본인 소유 소콘이 아님
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expired = now.plusHours(24);

        if (now.isAfter(socon.getExpiredAt())) {
            expired = socon.getExpiredAt();
        }

        List<Double> random = Arrays.asList(0.000001, 0.000002, 0.000003, 0.000004, 0.000005, 0.000006, 0.000007, 0.000008, 0.000009, 0.00001
                                        , -0.000001, -0.000002, -0.000003, -0.000004, -0.000005, -0.000006, -0.000007, -0.000008, -0.000009, -0.00001);
        Random ran = new Random();

        socon.setStatus(SoconStatus.sogon); // 소콘의 상태를 "sogon"으로 업데이트
        soconRepository.save(socon);

        sogonRepository.save(Sogon.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .expiredAt(expired)
                .isPicked(false)
                .image1(request.getImage1())
                .image2(request.getImage2())
                .memberId(memberId)
                .lat(request.getLat() + random.get(ran.nextInt(random.size())))
                .lng(request.getLng() + random.get(ran.nextInt(random.size())))
                .socon(socon)
                .build());
    }


    // 댓글 작성
    public void addSogonComment(Integer sogonId,
                                AddCommentRequest request,
                                int memberId) {

        Sogon sogon = sogonRepository.findById(sogonId)
                .orElseThrow(() -> new SogonException(SogonErrorCode.SOGON_NOT_FOUND));

        commentRepository.save(Comment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .isPicked(false)
                .sogon(sogon)
                .memberId(memberId)
                .build());

        try {
            notificationFeignClient.sendMessageMember(FcmMessage.builder()
                            .title("내 소곤에 댓글이 달렸어요!")
                            .body(request.getContent())
                            .memberId(sogon.getMemberId())
                    .build());
        } catch (RuntimeException e){
            log.error("소곤 댓글 알림 발송에 실패했습니다.");
        }
    }
    @Transactional
    // 소곤 댓글 채택
    public void pickSogonComment(Integer sogonId, Integer commentId, int memberId) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new SogonException(SogonErrorCode.COMMENT_NOT_FOUND));

        Sogon sogon = sogonRepository.findById(sogonId)
                .orElseThrow(() -> new SogonException(SogonErrorCode.SOGON_NOT_FOUND));

        if (!sogon.getMemberId().equals(memberId)) {
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        Socon socon = soconRepository.findById(sogon.getSocon().getId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));

        if (!(Objects.equals(socon.getStatus(), SoconStatus.sogon)) ) {
            throw new StoreException(StoreErrorCode.INVALID_SOCON);
        }

        // 소콘 소유권 이전
        socon.setMemberId(comment.getMemberId());
        socon.setStatus(SoconStatus.unused);
        comment.setIsPicked(true);
        sogon.setIsPicked(true);

        try {
            soconRepository.save(socon);
            commentRepository.save(comment);
            sogonRepository.save(sogon);
        } catch (RuntimeException e){
            throw new SogonException(SogonErrorCode.SOGON_FAIL);
        }

        try {
            notificationFeignClient.sendMessageMember(FcmMessage.builder()
                    .title("댓글이 채택됐어요!")
                    .body("["+sogon.getTitle()+"]\n"+"소곤의 댓글이 채택되어 소콘을 획득했습니다.")
                    .memberId(comment.getMemberId())
                    .build());
        } catch (RuntimeException e){
            log.error("소곤 댓글 채택 알림 발송에 실패했습니다.");
        }
    }

    // 소곤 상세 조회
    public Map<String, Object> getSogon(Integer id) {

        Sogon sogon = sogonRepository.findById(id)
                .orElseThrow(() -> new SogonException(SogonErrorCode.SOGON_NOT_FOUND));

        Socon socon = soconRepository.findById(sogon.getSocon().getId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));

        // 소곤 만료 시간
        LocalDateTime expiredAt = LocalDateTime.parse(sogon.getExpiredAt().toString());
        // 현재 시간
        LocalDateTime nowWithMilliseconds = LocalDateTime.now().withNano(0);
        Boolean isExpired;
        if (expiredAt.toLocalTime().isAfter(nowWithMilliseconds.toLocalTime())) {
            isExpired = true;
        }
        else{
            isExpired = false;
        }


        Member sogonOwner = feignServiceClient.getMemberInfo(sogon.getMemberId());

        List<CommentResponse> commentRepsonses = new ArrayList<>();
        List<Comment> comments = commentRepository.findAllBySogonId(id);
        for (Comment comment : comments) {

            Member commentOwner = feignServiceClient.getMemberInfo(comment.getMemberId());

            commentRepsonses.add(CommentResponse.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .memberName(commentOwner.getNickname())
                    .memberImg(commentOwner.getProfileUrl())
                    .isPicked(comment.getIsPicked())
                    .build());
        }

        return Map.of("sogon", SogonResponse.builder()
                        .id(sogon.getId())
                        .title(sogon.getTitle())
                        .memberName(sogonOwner.getNickname())
                        .memberImg(sogonOwner.getProfileUrl())
                        .content(sogon.getContent())
                        .image1(sogon.getImage1())
                        .image2(sogon.getImage2())
                        .soconImg(socon.getIssue().getImage())
                        .createdAt(sogon.getCreatedAt())
                        .expiredAt(sogon.getExpiredAt())
                        .isExpired(isExpired)
                        .build(),
                "comments", commentRepsonses);
    }

    // 작성 소곤 목록 조회
    public List<SogonListResponse> getMySogons(int memberId) {

        List<Sogon> sogons = sogonRepository.findAllByMemberId(memberId);
        List<SogonListResponse> sogonListResponses = new ArrayList<>();
        for (Sogon sogon : sogons) {
            Socon socon = soconRepository.findById(sogon.getSocon().getId())
                    .orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));

            sogonListResponses.add(SogonListResponse.builder()
                    .id(sogon.getId())
                    .title(sogon.getTitle())
                    .soconImg(socon.getIssue().getImage())
                    .createdAt(sogon.getCreatedAt())
                    .isExpired(sogon.getExpiredAt().isAfter(LocalDateTime.now()))
                    .isPicked(sogon.getIsPicked())
                    .build());
        }

        return sogonListResponses;

    }

    // 작성 댓글 목록 조회
    public List<CommentListResponse> getMyComments(int memberId) {

        List<Comment> comments = commentRepository.findAllByMemberId(memberId);
        List<CommentListResponse> commentListResponses = new ArrayList<>();
        for (Comment comment : comments) {

            commentListResponses.add(CommentListResponse.builder()
                    .id(comment.getId())
                    .title(comment.getSogon().getTitle())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .isPicked(comment.getIsPicked())
                    .build());
        }
        return commentListResponses;
    }

    // 범위 내 소곤 리스트 조회
    public Object getSogonList(Double x, Double y) {
        // 중심 좌표와 반경 1.5km 이내에 있는 Sogon 리스트 반환


        double radius = 1.5;
        double radiusInRadians = radius / 6371.0;

        if (x == null || y == null) {
            throw new IllegalArgumentException("Center coordinates cannot be null");
        }

        // 중심 좌표의 위도 경도 값
        double centerXInRadians = Math.toRadians(x);
        double centerYInRadians = Math.toRadians(y);

        // 반경 1.5km 이내의 위도 범위 계산
        double minLatitude = Math.toDegrees(centerXInRadians - radiusInRadians);
        double maxLatitude = Math.toDegrees(centerXInRadians + radiusInRadians);

        // 반경 1.5km 이내의 경도 범위 계산
        double minLongitude = Math.toDegrees(centerYInRadians - radiusInRadians / Math.cos(centerXInRadians));
        double maxLongitude = Math.toDegrees(centerYInRadians + radiusInRadians / Math.cos(centerXInRadians));

        // 중심 좌표를 중심으로 반경 1.5km 이내에 있는 sogon을 조회
        List<Sogon> sogons = sogonRepository.findByLatBetweenAndLngBetween(
                minLatitude, maxLatitude, minLongitude, maxLongitude);

        List<GetSogonListResponse> sogonListResponses = new ArrayList<>();

        for (Sogon sogon : sogons) {

            LocalDateTime expiredAt = sogon.getExpiredAt();

            LocalDateTime now = LocalDateTime.now();

            DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
            String formattedDateTime = now.format(formatter);
            LocalDateTime parsedDateTime = LocalDateTime.parse(formattedDateTime, formatter);
            Duration duration = Duration.between(parsedDateTime, expiredAt);
            // 시간이 남은 sogon만 출력
            if(duration.toHours() > 0) {
                Member member = feignServiceClient.getMemberInfo(sogon.getMemberId());

                sogonListResponses.add(GetSogonListResponse.builder()
                        .id(sogon.getId())
                        .title(sogon.getTitle())
                        .lat(sogon.getLat())
                        .lng(sogon.getLng())
                        .lastTime((int) duration.toHours())
                        .memberName(member.getNickname())
                        .commentCount(commentRepository.countBySogonId(sogon.getId()))
                        .soconImg(sogon.getSocon().getIssue().getImage())
                        .isPicked(sogon.getIsPicked())
                        .build()
                );
            }

        }

        return sogonListResponses;

    }
}
