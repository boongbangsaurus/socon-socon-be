package site.soconsocon.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soconsocon.auth.domain.dto.request.QnaRegisterRequestDto;
import site.soconsocon.auth.exception.MemberException;
import site.soconsocon.auth.service.QnaService;
import site.soconsocon.utils.MessageUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/qnas")
@Log4j2
public class QnaController {

    private final QnaService qnaService;

    @PostMapping("")
    public ResponseEntity registerQna(@RequestBody QnaRegisterRequestDto qnaRegisterRequestDto) throws MemberException {
        qnaService.saveQna(qnaRegisterRequestDto);
        return ResponseEntity.ok().body(MessageUtils.success(null, "200", "등록에 성공하였습니다."));

    }
}
