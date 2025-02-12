package com.ashok.explore.controllers;

import com.ashok.explore.config.Properties;
import com.ashok.explore.to.ResponseTO;
import com.ashok.explore.to.UserTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // Allow only Angular frontend
public class UserMgmtController {

    private final Properties properties;

    private final List<UserTO> USERS_DATA = new ArrayList<>(Arrays.asList(new UserTO(1L, "admin", "admin"), new UserTO(2L, "ashok", "ashok")));

    @GetMapping("/fetch-users")
    public ResponseEntity<?> getUsers() {
        log.info("Test message: {}", properties.getMessage());
        return ResponseEntity.ok(new ResponseTO<>("success", getMetadata(), USERS_DATA));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody UserTO user) {

        if (USERS_DATA.stream().anyMatch(u -> Objects.equals(u.getUsername(), user.getUsername()))) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseTO<>("fail", null, user));
        }
        user.setUserId((long) (Math.random() * 100));
        USERS_DATA.add(user);

        return ResponseEntity.ok(new ResponseTO<>("success", null, user));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserTO user) {

        Optional<UserTO> userTO = USERS_DATA.stream().filter(u -> Objects.equals(u.getUsername(), user.getUsername())).findFirst();
        if (userTO.isPresent()) {
            if (userTO.get().getPassword().equals(user.getPassword())) {
                return ResponseEntity.ok(new ResponseTO<>("success", null, user));
            }
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ResponseTO<>("fail", null, "Username and/or password are incorrect!"));

    }

    private Map<String, Object> getMetadata() {
        return Map.of("count", USERS_DATA.size());
    }
}
