package com.berk.table_tennis_tournament_management_backend.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam("username") String username,
                         @RequestParam("password") String password) {
        if (username.equals("ahmetbitirmis") && password.equals("35ahmetbitirmis35")) {
            Admin response = new Admin(1, "Ahmet Bitirmiş");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
