package site.soconsocon.notification.fcm.service;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import site.soconsocon.notification.fcm.domain.dto.request.FcmMessage;
import site.soconsocon.notification.fcm.domain.dto.request.SaveTokenRequest;
import site.soconsocon.notification.fcm.domain.entity.DeviceToken;
import site.soconsocon.notification.fcm.domain.entity.TokenStatus;
import site.soconsocon.notification.fcm.exception.FcmErrorCode;
import site.soconsocon.notification.fcm.exception.FcmException;
import site.soconsocon.notification.fcm.repository.jpa.FcmRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {
    private final FcmRepository fcmRepository;

    // 비밀키 경로 환경 변수
    @Value("${fcm.config}") private String projectConfig;
    // 프로젝트 아이디 환경 변수
    @Value("${fcm.project-id}") private String projectId;
    // 의존성 주입이 이루어진 후 초기화를 수행한다.
    @PostConstruct
    public void initialize() {
        FirebaseOptions options = null;
        try {

            InputStream credentialsStream = new ByteArrayInputStream(projectConfig.getBytes());
            options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialsStream))
                    .setProjectId(projectId)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("FCM 서버 세팅 중 에러가 발생했습니다: " + e);
        }
        if(FirebaseApp.getApps().isEmpty()){
            FirebaseApp.initializeApp(options);
        }
    }
    public String createCustomToken(String uid) {
        String customToken = null;

        try {
            customToken = FirebaseAuth.getInstance().createCustomToken(uid);
            log.info(customToken);
        } catch (Exception e) {
            throw new FcmException(FcmErrorCode.CREATE_TOKEN_FAIL);
        }

        return customToken;
    }

    public DeviceToken saveToken(SaveTokenRequest saveTokenRequest){
        DeviceToken deviceToken=saveTokenRequest.toEntity();
        try {
            fcmRepository.save(deviceToken);
        }catch (RuntimeException e){
            throw new FcmException(FcmErrorCode.SAVING_TOKEN_FAIL);
        }
        return deviceToken;
    }

    public void subscribeMyTokens(Integer userId, Long topicId) {
        List<DeviceToken> deviceTokenList = fcmRepository
                .findDeviceTokensByMemberId(userId)
                .orElseThrow(() -> new FcmException(FcmErrorCode.NO_EXIST_TOKEN));
        for(DeviceToken deviceToken : deviceTokenList) {
            subscribeByTopic(deviceToken.getDeviceToken(), String.valueOf(topicId));
        }
    }

    // 1. 로그인 시 새로운 디바이스 토큰이라면, 그룹아이디(topicName)에 token을 연결해줄 때 사용한다.
    // 2. 그룹에 가입될 때 사용한다.
    public void subscribeByTopic(String token, String topicName) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic(Collections.singletonList(token), topicName);
        } catch (FirebaseMessagingException e) {
            throw new FcmException(FcmErrorCode.SUBSCRIBE_FAIL);
        }
    }

    // 지정된 topic에 fcm를 보냄
    public void sendMessageByTopic(String title, String body, Long topicId) {
        try {
            FirebaseMessaging.getInstance().send(Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                            .setTopic(String.valueOf(topicId))
                    .build());
        } catch (FirebaseMessagingException | IllegalArgumentException e) {
            throw new FcmException(FcmErrorCode.CAN_NOT_SEND_NOTIFICATION);
        }
    }

    // 받은 token을 이용하여 fcm를 보냄
    public void sendMessageByMemberId(FcmMessage fcmMessage) {
        log.info(fcmMessage.toString());
        try {
            List<DeviceToken> tokenList = fcmRepository.findDeviceTokensByMemberId(fcmMessage.getMemberId())
                            .orElseThrow(() -> new FcmException(FcmErrorCode.NO_EXIST_TOKEN));

            for (DeviceToken token:tokenList) {
                if(token.getStatus() == TokenStatus.ACTIVE){
                    FirebaseMessaging.getInstance().send(Message.builder()
                            .setNotification(Notification.builder()
                                    .setTitle(fcmMessage.getTitle())
                                    .setBody(fcmMessage.getBody())
                                    .build())
                            .setToken(token.getDeviceToken())
                            .build());
                }
            }

        } catch (FirebaseMessagingException | IllegalArgumentException e) {
            log.warn(e.getMessage());
            throw new FcmException(FcmErrorCode.CAN_NOT_SEND_NOTIFICATION);
        }
    }

    // 모든 기기에 fcm를 보내는 메서드
    public void sendMessageAll(String title, String body) {
        List<DeviceToken> deviceTokenList = fcmRepository
                .findAllByStatus(TokenStatus.ACTIVE)
                .orElseThrow(() -> new FcmException(FcmErrorCode.NO_EXIST_TARGET));

        List<String> tokenList = new ArrayList<>();
        for(DeviceToken token : deviceTokenList){
            tokenList.add(token.getDeviceToken());
        }

        try {
            FirebaseMessaging.getInstance().sendMulticast(MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(tokenList)
                    .build());
        } catch (FirebaseMessagingException | IllegalArgumentException e) {
            throw new FcmException(FcmErrorCode.CAN_NOT_SEND_NOTIFICATION);
        }
    }
}
