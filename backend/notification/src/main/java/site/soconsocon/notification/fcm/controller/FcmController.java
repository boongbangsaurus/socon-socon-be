package site.soconsocon.notification.fcm.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soconsocon.notification.fcm.domain.dto.request.FcmMessage;
import site.soconsocon.notification.fcm.domain.dto.request.SaveTokenRequest;
import site.soconsocon.notification.fcm.domain.entity.DeviceToken;
import site.soconsocon.notification.fcm.service.FcmService;
import site.soconsocon.utils.MessageUtils;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/notification/fcm")
public class FcmController {
    private final FcmService fcmService;

    @PostMapping("/save")
    public ResponseEntity saveDeviceToken(@RequestBody SaveTokenRequest saveTokenRequest){
        DeviceToken savedDeviceToken = fcmService.saveToken(saveTokenRequest);
        return ResponseEntity.ok(MessageUtils.success(savedDeviceToken));
    }

    @PostMapping("/topic")
    public ResponseEntity sendMessageTopic(@RequestBody FcmMessage fcmMessage) {
        fcmService.sendMessageByTopic(fcmMessage.getTitle(), fcmMessage.getBody(), fcmMessage.getTopicId());
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @PostMapping("/user")
    public ResponseEntity sendMessageMember(@RequestBody FcmMessage fcmMessage) {
        fcmService.sendMessageByMemberId(fcmMessage);
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @PostMapping("/all")
    public ResponseEntity sendMessageAll(@RequestBody FcmMessage fcmMessage) {
        fcmService.sendMessageAll(fcmMessage.getTitle(), fcmMessage.getBody());
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @PostMapping("/subscribe")
    public ResponseEntity subscribeByTopic(@RequestBody FcmMessage fcmMessage) {
        fcmService.subscribeMyTokens(fcmMessage.getMemberId(), fcmMessage.getTopicId());
        return ResponseEntity.ok().body(MessageUtils.success());
    }

    @GetMapping("/authenticate")
    public ResponseEntity authenticate(@RequestHeader("X-Authorization-Id") int memberId) {
        String uidToken = fcmService.createCustomToken(String.valueOf(memberId));
        return ResponseEntity.ok(MessageUtils.success(uidToken));
    }
}
