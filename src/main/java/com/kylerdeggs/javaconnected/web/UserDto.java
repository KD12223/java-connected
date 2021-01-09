package com.kylerdeggs.javaconnected.web;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * User representation object.
 *
 * @author Kyler Deggs
 * @version 1.0.0
 */
public class UserDto {
    @NotNull
    private String id;

    private String firstName, lastName, email;

    @Size(min = 10, max = 10)
    private String phone;

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
