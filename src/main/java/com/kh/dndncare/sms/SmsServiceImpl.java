package com.kh.dndncare.sms;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;

@Service
public class SmsServiceImpl implements SmsService {

    private final DefaultMessageService messageService;

    public SmsServiceImpl(
            @Value("${coolsms.api.key}") String apiKey,
            @Value("${coolsms.api.secret}") String apiSecret
    ) {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
    }

    @Override
    public SingleMessageSentResponse sendSms(String to, String from, String text) {
        Message message = new Message();
        message.setFrom(from);
        message.setTo(to);
        message.setText(text);

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
