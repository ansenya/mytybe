package ru.senya.spot.controllers.search;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.senya.spot.models.dto.ChannelDto;
import ru.senya.spot.models.jpa.UserModel;
import ru.senya.spot.repos.jpa.ChannelRepository;
import ru.senya.spot.repos.jpa.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;

@RestController
@RequestMapping("channels")
public class ChannelSearchController {

    private final ChannelRepository channelRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final HashMap<UserModel, ArrayList<String>> searchHistory = new HashMap<>();


    @Autowired
    public ChannelSearchController(ChannelRepository channelRepository, UserRepository userRepository) {
        this.channelRepository = channelRepository;
        this.userRepository = userRepository;
        modelMapper = new ModelMapper();
    }

    @GetMapping("search")
    public ResponseEntity<?> searchChannels(Authentication authentication,
                                            @RequestParam(value = "q", required = false, defaultValue = "") String query,
                                            @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                            @RequestParam(value = "size", defaultValue = "10", required = false) int size) {
        PageRequest pageRequest = PageRequest.of(page, size);

        if (query.isEmpty()) {
            return ResponseEntity.ok(new PageImpl<>(new ArrayList<>(), pageRequest, 0));
        }

        var result = channelRepository.findAllByNameContaining(query);
        UserModel user;
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            user = userRepository.findByUsername(authentication.getName());
        } else {
            user = null;
        }

        var dtoList = result.stream()
                .map(channel -> {
                    var dto = modelMapper.map(channel, ChannelDto.class);
                    if (user != null) {
                        dto.setFollowedByThisUser(channel.getFollowers().contains(user));
                    }
                    return dto;
                })
                .toList();

        int fromIndex = (int) pageRequest.getOffset();
        int toIndex = Math.min((fromIndex + pageRequest.getPageSize()), dtoList.size());

        if (authentication != null) {
            if (userRepository.existsByUsername(authentication.getName())) {
                var history = searchHistory.get(user);
                if (history != null) {
                    if (history.size() > 4) {
                        history.remove(0);
                    }
                } else {
                    history = new ArrayList<>();
                }
                history.add(query);

                searchHistory.put(user, history);
            }
        }


        return ResponseEntity.ok(new PageImpl<>(dtoList.subList(fromIndex, toIndex), pageRequest, dtoList.size()));
    }

    @GetMapping("/searchHistory")
    public ResponseEntity<?> getSearchHistory(Authentication authentication) {
        if (authentication != null && userRepository.existsByUsername(authentication.getName())) {
            return ResponseEntity.ok(searchHistory.get(userRepository.findByUsername(authentication.getName())));
        }
        return ResponseEntity.status(401).build();
    }


}
