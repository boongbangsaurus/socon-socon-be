package site.soconsocon.socon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.socon.store.domain.dto.request.SoconBookSearchRequest;
import site.soconsocon.socon.store.domain.dto.response.SoconInfoResponse;
import site.soconsocon.socon.store.service.SoconService;
import site.soconsocon.utils.MessageUtils;


@RestController
@RequestMapping("/api/v1/socons")
@RequiredArgsConstructor
public class SoconApiController {

    private final SoconService soconService;

    // 소콘 상세 조회
    @GetMapping(value = "/{socon_id}" , produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getSoconInfo(
            @PathVariable("socon_id") Integer soconId
    ) {
        SoconInfoResponse socon = soconService.getSoconInfo(soconId);

        return ResponseEntity.ok().body(MessageUtils.success(socon));
    }

    // 소콘북 목록 조회
    @GetMapping(value = "/book", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> soconBook(
            @RequestHeader("X-Authorization-Id") int memberId
    ) {
        return ResponseEntity.ok().body(MessageUtils.success(soconService.getMySoconList(memberId)));
    }

    // 소콘 사용 승인
    @GetMapping(value = "/{socon_id}/approval" , produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> soconApproval(
            @PathVariable("socon_id") Integer soconId,
            @RequestHeader("X-Authorization-Id") int memberId) {

        soconService.soconApproval(soconId, memberId);

        return ResponseEntity.ok().body(MessageUtils.success());
    }

    // 소콘북 검색
    @PostMapping(value = "/book/search" , produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> soconBookSearch(
            @RequestBody SoconBookSearchRequest request,
            @RequestHeader("X-Authorization-Id") int memberId
    ) {


        return ResponseEntity.ok().body(MessageUtils.success(soconService.searchSocon(request, memberId)));
    }

    // 내 소콘, 소곤 개수 조회
    @GetMapping(value = "/mypage/{member_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> myPage(
        @PathVariable("member_id") Integer memberId
    ){
        return ResponseEntity.ok().body(MessageUtils.success(soconService.getMyPage(memberId)));
    }


}
