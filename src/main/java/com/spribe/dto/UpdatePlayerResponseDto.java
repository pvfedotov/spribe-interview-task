package com.spribe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = false)
public class UpdatePlayerResponseDto {
    @JsonProperty(value = "id", required = true)
    private Long id;

    @JsonProperty(value = "age", required = true)
    private Integer age;

    @JsonProperty(value = "gender", required = true)
    private String gender;

    @JsonProperty(value = "role", required = true)
    private String role;

    @JsonProperty(value = "screenName", required = true)
    private String screenName;

    @JsonProperty(value = "login", required = true)
    private String login;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
