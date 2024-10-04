package com.berk.table_tennis_tournament_management_backend.sms;

import com.berk.table_tennis_tournament_management_backend.StringHelper;
import com.berk.table_tennis_tournament_management_backend.match.Match;
import com.berk.table_tennis_tournament_management_backend.match.MatchRepository;
import com.berk.table_tennis_tournament_management_backend.participant.Participant;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
public class SMSService {
    @Value("${twilio.account-sid}")
    private String ACCOUNT_SID;
    @Value("${twilio.auth-token}")
    private String AUTH_TOKEN;
    private MatchRepository matchRepository;
    private PhoneNumber twilioNumber;

    public SMSService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
        this.twilioNumber = new PhoneNumber("+12082890229");
    }

    @PostConstruct
    private void setup() {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
    }

    // take age category as parameter
    public void sendMatchInfoSms() {
        List<Match> matches = matchRepository.findAll();
        for (Match match : matches) {
            Participant p1 = match.getP1();
            Participant p2 = match.getP2();
            LocalTime time = match.getStartTime();
            String formattedTime = String.format("%02d:%02d", time.getHour(), time.getMinute());
            String table = match.getTable().getName().split(" ")[1];

            sendSms(p1.getPhoneNumber(),
                    constructMatchMessage(
                            formattedTime,
                            table,
                            StringHelper.formatName(p1)));
            sendSms(p2.getPhoneNumber(),
                    constructMatchMessage(
                            formattedTime,
                            table,
                            StringHelper.formatName(p2)));
        }

    }

    public void sendSms(String destinationPhoneNumber, String body) {
        destinationPhoneNumber = destinationPhoneNumber.charAt(0) == '0' ?
                "+90" + destinationPhoneNumber.substring(1) : "+90" + destinationPhoneNumber;
        Message message =
                Message.creator(
                                new PhoneNumber(destinationPhoneNumber),
                                twilioNumber,
                                body)
                        .create();

        System.out.println(message.getBody());
    }

    private String constructMatchMessage(String time, String table, String participant) {
        return new StringBuilder()
                .append("Saat ")
                .append(time)
                .append("'da ")
                .append(table)
                .append(" numaralı masada ")
                .append(participant)
                .append(" ile maçınız var.")
                .toString();
    }
}