package com.berk.table_tennis_tournament_management_backend.tournament_img;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/tournament-img")
@AllArgsConstructor
public class TournamentImgController {

    private final TournamentImgRepository tournamentImgRepository;

    @GetMapping
    public ResponseEntity<byte[]> getImage() {
        Optional<TournamentImg> tournamentImg = tournamentImgRepository.findAll().stream().findFirst();
        if (tournamentImg.isPresent()) {
            // Set content type for the image (assuming it's a generic image like png/jpeg)
            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "image/jpeg"); // Change to the appropriate image type

            return new ResponseEntity<>(tournamentImg.get().getData(), headers, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> uploadImage(@RequestParam("tournamentName") String tournamentName,
                                              @RequestParam("image") MultipartFile image) {
        try {
            TournamentImg tournamentImage = new TournamentImg();
            tournamentImage.setTournamentName(tournamentName);
            tournamentImage.setData(image.getBytes());

            tournamentImgRepository.save(tournamentImage);
            return new ResponseEntity<>("Image uploaded successfully!", HttpStatus.OK);
        } catch (IOException e) {
            return new ResponseEntity<>("Failed to upload image", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
