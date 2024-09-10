package com.berk.table_tennis_tournament_management_backend.sms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SMS {
    private String destinationPhoneNumber;
    private String message;
}
