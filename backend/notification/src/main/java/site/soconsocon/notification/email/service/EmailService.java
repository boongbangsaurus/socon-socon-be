package site.soconsocon.notification.email.service;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.soconsocon.notification.email.config.EmailConfig;
import site.soconsocon.notification.email.domain.entity.CertificationNumber;
import site.soconsocon.notification.email.exception.EmailErrorCode;
import site.soconsocon.notification.email.exception.EmailException;
import site.soconsocon.notification.email.repository.redis.EmailCertificationRepository;
import site.soconsocon.notification.email.utils.CertificationUtil;
import site.soconsocon.notification.global.domain.dto.request.Member;
import site.soconsocon.notification.global.feign.MemberFeignClient;
import site.soconsocon.notification.global.feign.dto.response.MemberFeignResponse;

@RequiredArgsConstructor
@Slf4j
@Service
public class EmailService {

    private final EmailConfig emailConfig;
    private final CertificationUtil certificationUtil;
    private final EmailCertificationRepository repository;
    private final JavaMailSender mailSender;
    private final MemberFeignClient memberFeignClient;

    @Transactional
    public void sendCodeMail(String email){
        //이미 가입된 이메일인지 검증
        MemberFeignResponse user = memberFeignClient.getMemberByMemberEmail(email);
        //만약 이미 가입된 사용자가 존재하는 이메일이라면 전송 거부
        if(user!=null){
            throw new EmailException(EmailErrorCode.ALREADY_JOIN_EMAIL);
        }
        //인증번호 생성
        String number = certificationUtil.createNumber();
        log.debug("인증번호 생성 = {}", number);
        CertificationNumber certificationNumber = CertificationNumber
                .builder()
                .email(email)
                .number(number)
                .build();
        //레디스에 저장
        repository.save(certificationNumber);
        //메일 생성 및 전송
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true);
            messageHelper.setFrom(emailConfig.getUserName());
            messageHelper.setTo(email);
            messageHelper.setSubject("[소콘소콘] 이메일 인증 번호 안내 입니다.");
            messageHelper.setText(makeCodeTemplate(number),true);

            mailSender.send(message);
        } catch (MessagingException e) {
            throw new EmailException(EmailErrorCode.SEND_ERROR);
        }
    }

    @Transactional
    public void confirmCode(String email, String code){
        //email로 레디스에서 찾는다.
        CertificationNumber certificationNumber = repository.findById(email)
                .orElseThrow(()->new EmailException(EmailErrorCode.WRONG_CODE));
        //코드 확인
        if (certificationNumber.getNumber().equals(code)){
            //같을 경우
            repository.delete(certificationNumber);
        }else {
            //틀릴경우
            throw new EmailException(EmailErrorCode.DIFFERENT_NUMBER);
        }
    }

    private String makeCodeTemplate(String code){
        String mainColor = "#F8D461";
        String secondaryColor = "#B7E786";
        String title = "SOCON-SOCON_AUTH_SERVICE";
        String template =
                "<div style=\"font-family: 'Apple SD Gothic Neo', 'sans-serif' !important; width: 540px; height: 600px; border-top: 4px solid "+mainColor+"; margin: 100px auto; padding: 30px 0; box-sizing: border-box; color: "+secondaryColor+";\">"+
                        "\t<h1 style=\"margin: 0; padding: 0 5px; font-size: 28px; font-weight: 400;\">"+
                        "\t\t<span style=\"font-size: 15px; margin: 0 0 10px 3px;\">"+title+"</span><br />"+
                        "\t\t<span style=\"color: "+mainColor+";\">이메일 인증</span> 안내입니다."+
                        "\t</h1>"+
                        "\t<p style=\"font-size: 16px; line-height: 26px; margin-top: 50px; padding: 0 5px;\">"+
                        "\t\t안녕하세요.<br />"+
                        "\t\t요청하신 인증 번호가 생성되었습니다.<br />"+
                        "\t\t아래 <b style=\"color: "+mainColor+";\">'인증 번호'</b> 를 확인한 뒤, 인증해주세요<br />"+
                        "\t\t감사합니다."+
                        "\t</p>"+
                        "\t<p style=\"font-size: 16px; margin: 40px 5px 20px; line-height: 28px;\">"+
                        "\t\t인증 번호: <br />"+
                        "\t\t<span style=\"font-size: 24px;\">"+code+"</span>"+
                        "\t</p>"+
                        "\t<div style=\"border-top: 1px solid #DDD; padding: 5px;\">"+
                        "\t\t<p style=\"font-size: 13px; line-height: 21px; color: #555;\">"+
                        "\t\t\t만약 정상적으로 인증 되지 않는다면, 다시 요청해 주세요.<br />"+
                        "\t\t</p>"+
                        "\t</div>"+
                        "</div>";
        return template;
    }
}