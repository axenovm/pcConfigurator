package com.example.cursovaya.service;

import com.example.cursovaya.entity.User;
import com.example.cursovaya.exception.ResourceNotFoundException;
import com.example.cursovaya.repository.UserRepository;
import com.example.cursovaya.security.UserDetailsImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        User user = userRepository.findByLogin(login).orElseThrow(
                () -> new ResourceNotFoundException("User not found"));
        return new UserDetailsImpl(user);
    }

    public User createUser(User user) {

        if(userRepository.existsByLogin(user.getLogin())) {
            throw new ResourceNotFoundException("User already exists");
        }
        return userRepository.save(user);
    }

    public User addDescriptionToUser(Long id, String description) {
        User userToAddDescription = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id));
        userToAddDescription.setDescription(description);
        return userRepository.save(userToAddDescription);
    }


//    public UserResponse convertToResponse(User user) {
//        UserResponse userResponse = new UserResponse();
//        userResponse.setId(user.getId());
//        userResponse.setNickname(user.getNickname());
//        userResponse.setDescription(user.getDescription());
//        userResponse.setUserPcConfigs(pcConfigurationRepository.findByUserId(user.getId()).
//                stream().map(pcConfigurationService::convertToResponse).toList());
//        return userResponse;
//    }

//    public UserResponse getUserById(Long id) {
//        User user = userRepository.findById(id).orElseThrow(
//                () -> new ResourceNotFoundException("not found user bi id = " + id));
//        return convertToResponse(user);
//    }
//
//    public List<UserResponse> getAllUsers()
//    {
//        return userRepository.findAll().stream().map(this::convertToResponse).toList();
//    }
}
