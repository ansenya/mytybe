package ru.senya.mytybe.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.senya.mytybe.models.dto.UserDto;
import ru.senya.mytybe.models.jpa.UserModel;
import ru.senya.mytybe.repos.jpa.UserRepository;

import java.util.Objects;


@RestController
@RequestMapping("users")
public class UserController {

    UserRepository userRepository;
    ModelMapper modelMapper = new ModelMapper();


    @Autowired
    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @GetMapping("{id}")
    public ResponseEntity<?> getOne(@PathVariable Long id) {

        if (id == null) {
            return ResponseEntity.badRequest().body("id is null");
        }

        UserModel user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(modelMapper.map(user, UserDto.class));
    }

    @GetMapping
    public ResponseEntity<?> getALlUsers(@RequestParam(value = "page", required = false) Integer pageNum,
                                         @RequestParam(value = "size", required = false, defaultValue = "10") int pageSize,
                                         @RequestParam(value = "sort", required = false, defaultValue = "asc") String sort) {

        if (pageNum == null) {
            return ResponseEntity.badRequest().body("page is null");
        }

        Sort.Direction direction;

        if (Objects.equals(sort, "desc")) {
            direction = Sort.Direction.ASC;
        } else {
            direction = Sort.Direction.DESC;
        }

        PageRequest page = PageRequest.of(pageNum, pageSize, Sort.by(direction, "created"));
        Page<UserModel> userPage = userRepository.findAll(page);

        Page<UserDto> userDtoPage = userPage.map(user -> modelMapper.map(user, UserDto.class));

        return ResponseEntity.ok(userDtoPage);
    }
}
