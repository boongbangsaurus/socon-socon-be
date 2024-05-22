package site.soconsocon.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.soconsocon.auth.domain.dto.request.MemberLoginRequestDto;
import site.soconsocon.auth.domain.dto.request.MemberRegisterRequestDto;
import site.soconsocon.auth.domain.dto.response.MemberFeignResponse;
import site.soconsocon.auth.domain.dto.response.MemberLoginResponseDto;
import site.soconsocon.auth.domain.dto.response.MemberResponseDto;
import site.soconsocon.auth.domain.dto.response.MyPageResponseDto;
import site.soconsocon.auth.domain.entity.jpa.Member;
import site.soconsocon.auth.domain.entity.jpa.RefreshToken;
import site.soconsocon.auth.domain.entity.jpa.UserRole;
import site.soconsocon.auth.exception.ErrorCode;
import site.soconsocon.auth.exception.MemberException;
import site.soconsocon.auth.feign.NotificationFeignClient;
import site.soconsocon.auth.feign.SoconFeignClient;
import site.soconsocon.auth.feign.domain.dto.feign.MemberRoleRequest;
import site.soconsocon.auth.feign.domain.dto.feign.MyPageSoconResponse;
import site.soconsocon.auth.feign.domain.dto.feign.SaveTokenRequest;
import site.soconsocon.auth.repository.MemberJpaRepository;
import site.soconsocon.auth.repository.MemberQueryRepository;
import site.soconsocon.auth.repository.RefreshTokenRepository;
import site.soconsocon.auth.security.MemberDetailService;
import site.soconsocon.auth.security.MemberDetails;
import site.soconsocon.auth.util.JwtUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;


@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberJpaRepository memberRepository;

    private final MemberQueryRepository memberQueryRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    private final NotificationFeignClient notificationFeignClient;

    private final SoconFeignClient soconFeignClient;

    private final MemberDetailService memberDetailService;


    /**
     * 회원가입
     *
     * @param requestDto
     * @return
     */
    public Member register(MemberRegisterRequestDto requestDto) {
        //이메일 중복체크
        if (!dupleEmailCheck(requestDto.getEmail())) {
            throw new MemberException(ErrorCode.DUPLE_EMAIL); //이미 있는 이메일
        }
        //닉네임 중복체크
        if (!dupleNicknameCheck(requestDto.getNickname())) {
            throw new MemberException(ErrorCode.DUPLE_NICK); //이미 있는 닉네임
        }

        Member member = Member.builder()
                .email(requestDto.getEmail())
                .password(passwordEncoder.encode(requestDto.getPassword()))
                .name(requestDto.getName())
                .nickname(requestDto.getNickname())
                .role(UserRole.USER)
                .phoneNumber(requestDto.getPhoneNumber())
                .isAgreed(requestDto.isAgreed())
                .build();
        Member savedMember = null;
        try {
            savedMember = memberRepository.save(member);
        } catch (RuntimeException e) {
            throw new MemberException(ErrorCode.ACCOUNT_REGISTER_FAIL);
        }

        return savedMember;

    }

    /**
     * 이메일 중복검사
     *
     * @param email
     */
    public boolean dupleEmailCheck(String email) {
        if (memberQueryRepository.findMemberByEmail(email).isPresent()) {
            return false;
        }
        return true;
    }

    /**
     * 닉네임 중복검사
     *
     * @param nickname
     * @return
     */
    public boolean dupleNicknameCheck(String nickname) {
        if (memberQueryRepository.findMemberByNickname(nickname).isPresent()) {
            return false;
        }
        return true;
    }

    public MemberLoginResponseDto login(MemberLoginRequestDto loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();
        String fcmToken = loginDto.getFcmToken();
        log.info(loginDto.toString());

        Member member = getMemberByEmail(email);
        String memberId = String.valueOf(member.getId());

        MemberLoginResponseDto memberLoginResponseDto = null;
        // 로그인 요청한 유저로부터 입력된 패스워드 와 디비에 저장된 유저의 암호화된 패스워드가 같은지 확인.(유효한 패스워드인지 여부 확인)
        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new MemberException(ErrorCode.WRONG_PASSWORD); //틀린 비밀번호
        }

        // 유효한 패스워드가 맞는 경우, 로그인 성공으로 응답.(액세스 토큰을 포함하여 응답값 전달)
        MemberDetails memberDetails = (MemberDetails) memberDetailService.loadUserByUsername(memberId);
        String accessToken = jwtUtil.generateToken(memberDetails);
        String refreshToken = jwtUtil.generateRefreshToken(memberDetails);

        RefreshToken redis = new RefreshToken(member.getId(), refreshToken);
        refreshTokenRepository.save(redis);
        memberLoginResponseDto = new MemberLoginResponseDto(accessToken, refreshToken, member.getNickname());
        try {
            notificationFeignClient.saveDeviceToken(new SaveTokenRequest(member.getId(), fcmToken, "MOBILE"));
        } catch (RuntimeException e) {
            log.warn("디바이스 토큰 저장 실패");
        }
        return memberLoginResponseDto;
    }

    @Transactional
    public void updateRole(MemberRoleRequest memberRoleRequest) {
        Member member = memberQueryRepository.findMemberById(memberRoleRequest.getMemberId()).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        String updateRole = memberRoleRequest.getRole(); //변경된 권한
        UserRole userRole = UserRole.valueOf(updateRole);
        member.updateRole(userRole);
    }


    /**
     * 액세스 토큰 재발급
     *
     * @param memberDetails
     * @param refreshToken
     * @return
     * @throws IOException
     */
    public String createAccessToken(MemberDetails memberDetails, String refreshToken) {
        int memberId = memberDetails.getMember().getId();
        //Redis에 저장된 리프레시 토큰 가져오기
        RefreshToken refreshToken1 = refreshTokenRepository.findRefreshTokenByMemberId(memberId);
        String rt = refreshToken1.getRefreshToken();
        Optional<Member> result = memberQueryRepository.findMemberById(memberId);

        if (result.isPresent()) {
            Member member = result.get();
            //request 리프레시 토큰과 Redis에 있는 리프레시 토큰이 같다면
            if (rt.equals(refreshToken)) {
                //리프레시 토큰의 유효시간이 남아있다면
                if (!jwtUtil.isRefreshTokenExpired(String.valueOf(memberId))) {
                    return jwtUtil.generateToken(memberDetails);
                }
            }
        }
        return null;
    }

    /**
     * 리프레시 토큰 재발급
     *
     * @param memberDetails
     * @return
     */
    public String createRefreshToken(MemberDetails memberDetails) {
        String memberId = memberDetails.getMember().getId().toString();
        if (!jwtUtil.isRefreshTokenExpired(memberId)) {
            return jwtUtil.generateRefreshToken(memberDetails);
        }
        return null;
    }


    public MemberResponseDto getUserInfo(int memberId) {
        Member member = memberQueryRepository.findMemberById(memberId).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        MemberResponseDto memberResponseDto = new MemberResponseDto();
        memberResponseDto.setEmail(member.getEmail());
        memberResponseDto.setNickname(member.getNickname());
        memberResponseDto.setName(member.getName());
        memberResponseDto.setPhoneNumber(member.getPhoneNumber());
        memberResponseDto.setSoconMoney(member.getSoconMoney());
        memberResponseDto.setSoconPassword(member.getSoconPassword());

        return memberResponseDto;
    }

    public Member getMemberByEmail(String email) {
        Member member = memberQueryRepository.findMemberByEmail(email).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        return member;
    }

    public MemberFeignResponse findMemberByMemberId(int memberId) {
        Member member = memberQueryRepository.findMemberById(memberId).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );
        MemberFeignResponse memberFeignResponse = new MemberFeignResponse();
        memberFeignResponse.setMemberId(memberId);
        memberFeignResponse.setEmail(member.getEmail());
        memberFeignResponse.setNickname(member.getNickname());
        memberFeignResponse.setName(member.getName());
        memberFeignResponse.setPhoneNumber(member.getPhoneNumber());
        memberFeignResponse.setProfileUrl(member.getProfileUrl());
        memberFeignResponse.setAccount(member.getAccountNo());
        memberFeignResponse.setSoconMoney(member.getSoconMoney());
        memberFeignResponse.setSoconPassword(member.getSoconPassword());

        return memberFeignResponse;
    }

    public MyPageResponseDto getMyPage(int memberId) {
        Member member = memberQueryRepository.findMemberById(memberId).orElseThrow(
                () -> new MemberException(ErrorCode.USER_NOT_FOUND)
        );

        MyPageSoconResponse response = soconFeignClient.myPage(memberId).getBody();
        log.info("response: {}", response);

        MyPageResponseDto myPageResponseDto = new MyPageResponseDto();

        myPageResponseDto.setEmail(member.getEmail());
        myPageResponseDto.setNickname(member.getNickname());
        myPageResponseDto.setName(member.getName());
        myPageResponseDto.setPhoneNumber(member.getPhoneNumber());
        myPageResponseDto.setProfileUrl(member.getProfileUrl());
        myPageResponseDto.setAccount(member.getAccountNo());
        myPageResponseDto.setSoconMoney(member.getSoconMoney());
        myPageResponseDto.setSoconPassword(member.getSoconPassword());
        myPageResponseDto.setSoconCnt(response.getSoconCnt()); //보유 소콘
        myPageResponseDto.setSogonCnt(response.getSogonCnt()); //작성 소곤
        myPageResponseDto.setSogonReplyCnt(response.getSogonReplyCnt()); //댓글 소곤

        return myPageResponseDto;
    }

    /**
     * 회원 정보 수정
     */
//    public void modifyMember(MemberModifyRequestDto requestDto) {
//        memberRepository.updateMember(requestDto.getName(), requestDto.getNickname(), requestDto.getMemo(), requestDto.getProfileUrl());
//    }
}
