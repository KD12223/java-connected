package com.kylerdeggs.javaconnected.web.dtos;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User representation object.
 *
 * @author Kyler Deggs
 * @version 1.1.0
 */
public class UserDto {
    @NotNull
    private final String id;

    private final String firstName, lastName, email;

    @Size(min = 10, max = 10)
    private String phone;

    public UserDto(String id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
