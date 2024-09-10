package com.berk.table_tennis_tournament_management_backend.sms;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sms")
@AllArgsConstructor
public class SMSController {

    private final SMSService smsService;

    @PostMapping("/send")
    public void sendSMS(@RequestBody SMS sms) {
        smsService.sendSms(sms.getDestinationPhoneNumber(), sms.getMessage());
    }
}
