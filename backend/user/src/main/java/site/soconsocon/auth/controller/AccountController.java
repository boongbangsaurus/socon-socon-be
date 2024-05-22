package site.soconsocon.auth.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.auth.domain.dto.request.AccountNoRequestDto;
import site.soconsocon.auth.domain.dto.request.DepositRequestDto;
import site.soconsocon.auth.domain.dto.request.WithdrawRequestDto;
import site.soconsocon.auth.exception.AccountException;
import site.soconsocon.auth.exception.MemberException;
import site.soconsocon.auth.service.AccountService;
import site.soconsocon.utils.MessageUtils;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
@Log4j2
public class AccountController {

    private final AccountService accountService;

    //소콘머니 증가
    @PutMapping("/deposit")
    public ResponseEntity deposit(@RequestBody DepositRequestDto depositRequestDto) throws MemberException, AccountException {
        return ResponseEntity.ok().body(MessageUtils.success(accountService.deposit(depositRequestDto)));
    }


    //소콘머니 출금
    @PutMapping("/withdraw")
    public ResponseEntity withdraw(@RequestBody WithdrawRequestDto withdrawRequestDto) throws AccountException, MemberException {
        return ResponseEntity.ok().body(MessageUtils.success(accountService.withdraw(withdrawRequestDto)));

    }

    //출금 계좌번호 등록 API
    @PutMapping("")
    public ResponseEntity registerAccount(@RequestHeader("X-Authorization-Id") int memberId, @RequestBody AccountNoRequestDto accountNoRequestDto) throws AccountException, MemberException {
        return ResponseEntity.ok().body(MessageUtils.success(accountService.saveAccountNo(memberId, accountNoRequestDto)));

    }
}

