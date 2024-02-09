package ru.senya.spot.models.jpa;

import lombok.Data;

@Data
public class UserRequest {
    private String username;
    private String password;
    private String name;
    private String surname;
    private String sex;
    private Integer age;
    private String country;

    public boolean isEmpty() {
        return username == null
                && password == null
                && name == null
                && surname == null
                && sex == null
                && age == null
                && country == null;
    }
}