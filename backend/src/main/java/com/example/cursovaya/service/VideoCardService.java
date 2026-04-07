package com.example.cursovaya.service;

import com.example.cursovaya.DTO.request.VideoCardRequest;
import com.example.cursovaya.DTO.response.VideoCardResponse;
import com.example.cursovaya.entity.VideoCard;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.VideoCardRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VideoCardService {
    private final VideoCardRepository videoCardRepository;

    public VideoCardService(VideoCardRepository videoCardRepository) {
        this.videoCardRepository = videoCardRepository;
    }

    public VideoCardResponse getVideoCardById(Long id){
        return convertToResponse(videoCardRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("not found videocard with id = " + id)));
    }

    public List<VideoCardResponse> getAllVideoCards(){
        return videoCardRepository.findAll().stream().map(this::convertToResponse).toList();
    }

    public VideoCardResponse convertToResponse(VideoCard videoCard){
        VideoCardResponse videoCardResponse = new VideoCardResponse();

        videoCardResponse.setId(videoCard.getVideoCardId());
        videoCardResponse.setGraphicsProcessor(videoCard.getGraphicsProcessor());
        videoCardResponse.setPrice(videoCard.getPrice());
        videoCardResponse.setModel(videoCard.getVideoCardModel());

        return  videoCardResponse;
    }
}
