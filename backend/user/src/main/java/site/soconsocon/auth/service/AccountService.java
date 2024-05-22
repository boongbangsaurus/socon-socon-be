package site.soconsocon.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.soconsocon.auth.domain.dto.request.AccountNoRequestDto;
import site.soconsocon.auth.domain.dto.request.DepositRequestDto;
import site.soconsocon.auth.domain.dto.request.WithdrawRequestDto;
import site.soconsocon.auth.domain.entity.jpa.Member;
import site.soconsocon.auth.exception.AccountException;
import site.soconsocon.auth.exception.ErrorCode;
import site.soconsocon.auth.exception.MemberException;
import site.soconsocon.auth.repository.MemberJpaRepository;
import site.soconsocon.auth.repository.MemberQueryRepository;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final MemberJpaRepository memberRepository;

    private final MemberQueryRepository memberQueryRepository;


    /**
     * 입금
     *
     * @param depositRequestDto
     * @throws MemberException
     */
    @Transactional
    public String deposit(DepositRequestDto depositRequestDto) throws MemberException, AccountException {
        Member member = memberQueryRepository.findMemberById(depositRequestDto.getMemberId()).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        int depositMoney = depositRequestDto.getMoney(); //충전할 소콘머니

        try {
            memberQueryRepository.chargeSoconMoney(depositRequestDto.getMemberId(), depositMoney); // 증가
            // 입금이 성공했다는 결과를 보냄
            return "소콘머니가 충전되었습니다.";

        } catch (Exception e) {
            // 입금이 실패했다는 결과를 보냄
            throw new AccountException(ErrorCode.DEPOSIT_FAIL);
        }

    }

    /**
     * 출금
     *
     * @param withdrawRequestDto
     * @throws MemberException
     * @throws AccountException
     */

    @Transactional
    public String withdraw(WithdrawRequestDto withdrawRequestDto) throws MemberException, AccountException {
        Member member = memberQueryRepository.findMemberById(withdrawRequestDto.getMemberId()).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        int soconMoney = member.getSoconMoney(); //현재 보유하고 있는 소콘머니
        int depositMoney = withdrawRequestDto.getMoney(); //출금할 소콘머니
        String soconPassword = member.getSoconPassword();
        String inputPassword = withdrawRequestDto.getSoconPassword(); //사용자가 입력한 비밀번호

        //소콘 비밀번호 맞지 않음
        if (!soconPassword.equals(inputPassword)) {
            throw new AccountException(ErrorCode.NOT_MATCH_PASSWORD);
        }
        //돈 없음
        if (soconMoney < depositMoney) {
            throw new AccountException(ErrorCode.NO_MONEY);
        }

        try {
            memberQueryRepository.withdrawSoconMoney(withdrawRequestDto.getMemberId(), depositMoney); //출금
            return "소콘머니가 출금되었습니다.";

        } catch (Exception e) {
            // 출금이 실패했다는 결과를 보냄
            throw new AccountException(ErrorCode.WITHDRAW_FAIL);
        }
    }

    /**
     * 출금 계좌번호, 비밀번호 등록
     * @param memberId
     * @param accountNoRequestDto
     * @return
     * @throws MemberException
     * @throws AccountException
     */
    @Transactional
    public String saveAccountNo(int memberId, AccountNoRequestDto accountNoRequestDto) throws MemberException, AccountException {
        Member member = memberQueryRepository.findMemberById(memberId).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        String accountNo = accountNoRequestDto.getAccountNo(); //계좌번호
        String soconPassword = accountNoRequestDto.getSoconPassword(); //소콘 비번

        try {
            member.setAccountNo(accountNo);
            member.setSoconPassword(soconPassword);

            memberRepository.save(member);
            return "계좌번호가 등록되었습니다.";

        } catch (Exception e) {
            // 계좌 등록이 실패했다는 결과를 보냄
            throw new AccountException(ErrorCode.ACCOUNT_REGISTER_FAIL);
        }
    }

}
