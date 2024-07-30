package com.kh.dndncare.sms;

import net.nurigo.sdk.message.response.SingleMessageSentResponse;

public interface SmsService {
	 SingleMessageSentResponse sendSms(String to, String from, String text);
}
