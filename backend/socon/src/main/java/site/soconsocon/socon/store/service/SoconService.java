package site.soconsocon.socon.store.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import site.soconsocon.socon.global.domain.ErrorCode;
import site.soconsocon.socon.global.exception.SoconException;
import site.soconsocon.socon.sogon.domain.entity.jpa.Sogon;
import site.soconsocon.socon.sogon.repository.jpa.CommentRepository;
import site.soconsocon.socon.sogon.repository.jpa.SogonRepository;
import site.soconsocon.socon.store.domain.dto.request.ChargeRequest;
import site.soconsocon.socon.store.domain.dto.request.SoconBookSearchRequest;
import site.soconsocon.socon.store.domain.dto.response.SoconInfoResponse;
import site.soconsocon.socon.store.domain.dto.response.SoconListResponse;
import site.soconsocon.socon.store.domain.dto.response.SoconMypageResponse;
import site.soconsocon.socon.store.domain.entity.jpa.Issue;
import site.soconsocon.socon.store.domain.entity.jpa.Item;
import site.soconsocon.socon.store.domain.entity.jpa.Socon;
import site.soconsocon.socon.store.domain.entity.jpa.SoconStatus;
import site.soconsocon.socon.store.exception.StoreErrorCode;
import site.soconsocon.socon.store.exception.StoreException;
import site.soconsocon.socon.store.feign.FeignServiceClient;
import site.soconsocon.socon.store.repository.jpa.SoconRepository;

import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class SoconService {

    private final SoconRepository soconRepository;
    private final FeignServiceClient feignServiceClient;
    private final SogonRepository sogonRepository;
    private final CommentRepository commentRepository;


    // 소콘 상세 조회
    public SoconInfoResponse getSoconInfo(Integer soconId) {

        Socon socon = soconRepository.findById(soconId).orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));
        Issue issue = socon.getIssue();
        Item item = issue.getItem();

        return SoconInfoResponse.builder()
                .itemName(issue.getName())
                .storeName(item.getStore().getName())
                .purchasedAt(socon.getPurchasedAt())
                .expiredAt(socon.getExpiredAt())
                .status(socon.getStatus())
                .description(item.getDescription())
                .image(item.getImage())
                .build();
    }

    // 소콘북 목록 조회
    public Map<String, Object> getMySoconList(
            int memberId
    ) {
        List<SoconListResponse> usableSocons = new ArrayList<>();
        List<SoconListResponse> unusableSocons = new ArrayList<>();

        List<Socon> unused = soconRepository.getUnusedSoconByMemberId(memberId);
        for (Socon socon : unused) {
            Issue issue = socon.getIssue();
            Item item = issue.getItem();

            LocalDateTime nowWithMilliseconds = LocalDateTime.now().withNano(0);

             if (socon.getExpiredAt().toLocalDate().isEqual(nowWithMilliseconds.toLocalDate()) || socon.getExpiredAt().toLocalTime().isAfter(nowWithMilliseconds.toLocalTime())){
                socon.setStatus(SoconStatus.expired);
                soconRepository.save(socon);
            }

            else{
                usableSocons.add(SoconListResponse.builder()
                        .soconId(socon.getId())
                        .itemName(issue.getName())
                        .storeName(item.getStore().getName())
                        .expiredAt(socon.getExpiredAt())
                        .status(socon.getStatus())
                        .itemImage(socon.getIssue().getItem().getImage())
                        .build());
                }
            }

        List<Socon> used = soconRepository.getUsedSoconByMemberId(memberId);
        for (Socon socon : used) {
            Issue issue = socon.getIssue();
            Item item = issue.getItem();

            // 소곤에 올라가있는 만료 소콘 상태 업데이트
            if (socon.getStatus().equals(SoconStatus.sogon)) {
                Sogon sogon = sogonRepository.findBySoconId(socon.getId());
                LocalDateTime currentTime = LocalDateTime.now();

                if (sogon.getExpiredAt().isBefore(currentTime)) {
                    // 만료 시간이 현재 시간 이전이면 상태를 "unused"로 업데이트
                    socon.setStatus(SoconStatus.unused);
                    soconRepository.save(socon);
                    usableSocons.add(SoconListResponse.builder()
                            .soconId(socon.getId())
                            .itemName(issue.getName())
                            .storeName(item.getStore().getName())
                            .expiredAt(socon.getExpiredAt())
                            .status(socon.getStatus())
                            .itemImage(socon.getIssue().getItem().getImage())
                            .build());
                }
                else{
                    unusableSocons.add(SoconListResponse.builder()
                            .soconId(socon.getId())
                            .itemName(issue.getName())
                            .storeName(item.getStore().getName())
                            .expiredAt(socon.getExpiredAt())
                            .status(socon.getStatus())
                            .itemImage(socon.getIssue().getItem().getImage())
                            .build());
                }
            }
            else{
                unusableSocons.add(SoconListResponse.builder()
                        .soconId(socon.getId())
                        .itemName(issue.getName())
                        .storeName(item.getStore().getName())
                        .expiredAt(socon.getExpiredAt())
                        .status(socon.getStatus())
                        .itemImage(socon.getIssue().getItem().getImage())
                        .build());
            }

        }

        Map<String, Object> response = new HashMap<>();
        response.put("usable", usableSocons);
        response.put("unusable", unusableSocons);

        return response;
    }

    // 소콘 사용 승인
    public void soconApproval(
            Integer soconId,
            int memberId
    ) {
        Socon socon = soconRepository.findById(soconId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.SOCON_NOT_FOUND));

        if (!Objects.equals(socon.getIssue().getItem().getStore().getMemberId(), memberId)) {
            // 요청자가 해당 점포 주인이 아닌 경우
            throw new SoconException(ErrorCode.FORBIDDEN);
        }

        LocalDateTime expiredAt = LocalDateTime.parse(socon.getExpiredAt().toString());
        LocalDateTime nowWithMilliseconds = LocalDateTime.now().withNano(0);
        if (expiredAt.toLocalDate().isEqual(nowWithMilliseconds.toLocalDate()) && expiredAt.toLocalTime().isAfter(nowWithMilliseconds.toLocalTime())){
            socon.setStatus(SoconStatus.expired);
            soconRepository.save(socon);
        }
        if (Objects.equals(socon.getStatus(), "unused")) {
            socon.setStatus(SoconStatus.used);
            socon.setUsedAt(LocalDateTime.now());

            // 출금 요청
            feignServiceClient.deposit(ChargeRequest.builder()
                    .memberId(memberId)
                    .money(socon.getIssue().getPrice())
                    .build());

            soconRepository.save(socon);
        } else {
            // 소곤에 등록된 경우, 만료 기간이 지난 경우, 사용된 상태인 경우 등등.
            throw new StoreException(StoreErrorCode.INVALID_SOCON);
        }


    }

    // 소콘북 검색
    public List<SoconListResponse> searchSocon(SoconBookSearchRequest request, int memberId) {
        List<Socon> socons;
        if (Objects.equals(request.getCategory(), "store")) {
            socons = soconRepository.getSoconByMemberIdAndStoreName(memberId, request.getKeyword());
        } else if (Objects.equals(request.getCategory(), "item")) {
            socons = soconRepository.getSoconByMemberIdAndItemName(memberId, request.getKeyword());
        } else {
            throw new SoconException(ErrorCode.BAD_REQUEST);
        }

        List<SoconListResponse> soconListResponses = new ArrayList<>();
        for (Socon socon : socons) {
            soconListResponses.add(SoconListResponse.builder()
                    .soconId(socon.getId())
                    .itemName(socon.getIssue().getName())
                    .storeName(socon.getIssue().getStoreName())
                    .expiredAt(socon.getExpiredAt())
                    .status(socon.getStatus())
                    .itemImage(socon.getIssue().getImage())
                    .build());
        }
        return soconListResponses;
    }

    // 보유 소콘, 소곤 개수 조회
    public Object getMyPage(Integer memberId) {

        return SoconMypageResponse.builder()
                .socon(soconRepository.getMySoconCount(memberId))
                .sogon(sogonRepository.getMySogonCount(memberId))
                .comment(commentRepository.getMyCommentCount(memberId))
                .build();
    }
}
