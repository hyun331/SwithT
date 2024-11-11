package com.tweety.SwithT.common.service;

import com.tweety.SwithT.common.configs.TwilioConfig;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TwilioService {

    private final TwilioConfig twilioConfig;

    @Autowired
    public TwilioService(TwilioConfig twilioConfig) {
        this.twilioConfig = twilioConfig;
    }

    public void sendSms(String to, String content) {
        Message message = Message.creator(
                        new PhoneNumber(to),  // 수신자 번호
                        new PhoneNumber(twilioConfig.getPhoneNumber()),  // Twilio에서 구입한 전화번호
                        content)  // 메시지 내용
                .create();

        System.out.println("메시지 전송 ID: " + message.getSid());
    }
}
