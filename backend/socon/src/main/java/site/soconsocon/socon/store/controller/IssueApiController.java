package site.soconsocon.socon.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.socon.store.domain.dto.request.AddMySoconRequest;
import site.soconsocon.socon.store.service.IssueService;
import site.soconsocon.utils.MessageUtils;

@RestController
@RequestMapping("/api/v1/issues")
@RequiredArgsConstructor
public class IssueApiController {

    private final IssueService issueService;

    // 소콘북 저장
    @PostMapping(value = "/socon", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> saveMySocon(
            @RequestBody AddMySoconRequest request
    ){
        issueService.saveMySocon(request);

        return ResponseEntity.ok().body(MessageUtils.success(null, "201 CREATED", null));
    }

    // 소콘 발행 중지
    @PutMapping(value = "/{issue_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> stopIssue(
            @PathVariable("issue_id") Integer issueId,
            @RequestHeader("X-Authorization-Id") int memberId
    ){
        issueService.stopIssue(issueId, memberId);

        return ResponseEntity.ok().body(MessageUtils.success(null, "204 NO CONTENT", null));
    }

    // 발행 정보 상세 조회
    @GetMapping(value = "/{issue_id}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Object> getIssueInfo(
            @PathVariable("issue_id") Integer issueId
    ){
        return ResponseEntity.ok().body(MessageUtils.success(issueService.getIssueInfo(issueId)));
    }
}
