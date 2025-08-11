package com.spribe.models;

import com.spribe.dto.CreatePlayerResponseDto;

public class Player {
    protected Long id;
    protected Integer age;
    protected String gender;
    protected String role;
    protected String screenName;
    protected String login;
    protected String password;

    public Long getId() {
        return id;
    }

    public Player setId(Long id) {
        this.id = id;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Player setAge(Integer age) {
        this.age = age;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Player setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public String getRole() {
        return role;
    }

    public Player setRole(String role) {
        this.role = role;
        return this;
    }

    public String getScreenName() {
        return screenName;
    }

    public Player setScreenName(String screenName) {
        this.screenName = screenName;
        return this;
    }

    public String getLogin() {
        return login;
    }

    public Player setLogin(String login) {
        this.login = login;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Player setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreatePlayerResponseDto)) return false;
        CreatePlayerResponseDto that = (CreatePlayerResponseDto) o;
        return java.util.Objects.equals(id, that.id) &&
            java.util.Objects.equals(login, that.login) &&
            java.util.Objects.equals(password, that.password) &&
            java.util.Objects.equals(screenName, that.screenName) &&
            java.util.Objects.equals(gender, that.gender) &&
            java.util.Objects.equals(age, that.age) &&
            java.util.Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(id, login, password, screenName, gender, age, role);
    }

    @Override
    public String toString() {
        return "Player{" +
            "id=" + id +
            ", age=" + age +
            ", gender='" + gender + '\'' +
            ", role='" + role + '\'' +
            ", screenName='" + screenName + '\'' +
            ", login='" + login + '\'' +
            ", password='" + password + '\'' +
            '}';
    }
}
