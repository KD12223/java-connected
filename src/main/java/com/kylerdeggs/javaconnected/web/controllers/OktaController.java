package com.kylerdeggs.javaconnected.web.controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kylerdeggs.javaconnected.service.UserService;
import com.kylerdeggs.javaconnected.web.dtos.UserDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Controller to handle Okta Event Hook requests.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
@RestController
@RequestMapping(path = "v1/api/okta")
public class OktaController {
    private final UserService userService;

    @Value(value = "${okta.events.secret}")
    private String eventAuthKey;

    @Autowired
    public OktaController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping(path = {"/create", "/update"})
    public Map<String, String> verify(@RequestHeader("X-Okta-Verification-Challenge") String challenge,
                                      @RequestHeader("Authorization") String key) {
        verifyAuthentication(key);
        return Collections.singletonMap("verification", challenge);
    }

    @PostMapping("/create")
    public void createUser(@RequestBody List<Object> request,
                           @RequestHeader("Authorization") String key) {
        verifyAuthentication(key);
        userService.createUser(convertToUserDto(request));
    }

    @PostMapping("/update")
    public void updateUser(@RequestBody List<Object> request,
                           @RequestHeader("Authorization") String key) {
        verifyAuthentication(key);
        userService.updateUser(convertToUserDto(request));
    }

    /**
     * Converts an Okta Event Hook request into a User DTO.
     *
     * @param oktaObject Okta Event Hook request body
     * @return User DTO
     */
    private UserDto convertToUserDto(List<Object> oktaObject) {
        @SuppressWarnings("unchecked") final Map<String, Object> rawRequest = (Map<String, Object>) oktaObject.get(0);

        @SuppressWarnings("unchecked") final List<Object> target = (List<Object>) rawRequest.get("target");

        final OktaActor oktaActor = new ObjectMapper().convertValue(target.get(0), OktaActor.class);
        final String[] displayNameSplit = oktaActor.getDisplayName().split(" ");

        return new UserDto(oktaActor.getId(), displayNameSplit[0], displayNameSplit[1], oktaActor.getAlternateId());
    }

    /**
     * Verifies Okta Event Hook authorization.
     *
     * @param eventAuthKey Authorization key
     */
    private void verifyAuthentication(String eventAuthKey) {
        if (!eventAuthKey.equals(this.eventAuthKey))
            throw new SecurityException("Okta Event Hook authorization key is invalid");
    }

    /**
     * Okta Event Hook representation object.
     *
     * @author Kyler Deggs
     * @version 1.0.0
     */
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class OktaActor {
        private String id, type, alternateId, displayName;

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getAlternateId() {
            return alternateId;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
