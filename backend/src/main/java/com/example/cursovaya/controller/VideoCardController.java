package com.example.cursovaya.controller;

import com.example.cursovaya.DTO.response.VideoCardResponse;
import com.example.cursovaya.service.VideoCardService;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("videocard")
public class VideoCardController {
    private static final Logger log = Logger.getLogger(VideoCardController.class.getName());

    private final VideoCardService videoCardService;

    public VideoCardController(VideoCardService videoCardService) {
        this.videoCardService = videoCardService;
    }

    @GetMapping("{id}")
    public ResponseEntity<VideoCardResponse> getVideoCardById(@PathVariable Long id){
        log.log(Level.INFO, "called getVideoCardById");
        return ResponseEntity.ok(videoCardService.getVideoCardById(id));
    }

    @GetMapping()
    public ResponseEntity<List<VideoCardResponse>> getAllVideoCards(){
        log.log(Level.INFO, "called getAllVideoCards");
        return ResponseEntity.ok(videoCardService.getAllVideoCards());
    }
}
