package site.soconsocon.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.soconsocon.auth.domain.dto.request.QnaRegisterRequestDto;
import site.soconsocon.auth.domain.entity.jpa.Member;
import site.soconsocon.auth.domain.entity.jpa.Qna;
import site.soconsocon.auth.exception.ErrorCode;
import site.soconsocon.auth.exception.MemberException;
import site.soconsocon.auth.repository.MemberJpaRepository;
import site.soconsocon.auth.repository.MemberQueryRepository;
import site.soconsocon.auth.repository.QnaRepository;

@Service
@RequiredArgsConstructor
public class QnaService {

    private final QnaRepository qnaRepository;
    private final MemberJpaRepository memberRepository;
    private final MemberQueryRepository memberQueryRepository;

    public void saveQna(QnaRegisterRequestDto qnaRegisterRequestDto) throws MemberException {
        Member member = memberQueryRepository.findMemberById(qnaRegisterRequestDto.getMemberId()).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );

        Qna qna = Qna.builder()
                .title(qnaRegisterRequestDto.getTitle())
                .content(qnaRegisterRequestDto.getContent())
                .member(member)
                .build();

        qnaRepository.save(qna);
    }
}
