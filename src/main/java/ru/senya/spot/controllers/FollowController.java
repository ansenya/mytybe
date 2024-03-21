package ru.senya.spot.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.senya.spot.models.dto.ChannelDto;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.PlaylistRepository;
import ru.senya.spot.repos.jpa.UserRepository;

@RestController
@RequestMapping("channels")
public class FollowController {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public FollowController(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        modelMapper = new ModelMapper();
    }

    @PostMapping("follow")
    public ResponseEntity<?> followChannel(Authentication authentication,
                                           @RequestParam("channelId") Long channelId) {
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            var user = userRepository.findByUsername(authentication.getName());
            var optChannel = channelRepository.findById(channelId);
            if (optChannel.isPresent()) {
                var channel = optChannel.get();
                if (channel.getUser().equals(user)) {
                    return ResponseEntity.status(409).body("you cannot follow your own channel");
                }
                if (channel.getFollowers().contains(user)) {
                    user.getSubscriptions().remove(channel);
                    channel.getFollowers().remove(user);
                } else {
                    user.getSubscriptions().add(channel);
                    channel.getFollowers().add(user);
                }
                userRepository.save(user);
                channelRepository.save(channel);
                return ResponseEntity.ok(modelMapper.map(channel, ChannelDto.class));
            } else {
                return ResponseEntity.status(404).body("channel not found");
            }
        } else {
            return ResponseEntity.status(401).build();
        }
    }
}
