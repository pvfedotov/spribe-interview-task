package com.spribe.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spribe.models.Player;

import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = false)
public class GetAllPlayersResponseDto {
    @JsonProperty(value = "id", required = true)
    private Long id;

    @JsonProperty(value = "screenName", required = true)
    private String screenName;

    @JsonProperty(value = "gender", required = true)
    private String gender;

    @JsonProperty(value = "age", required = true)
    private Integer age;

    public GetAllPlayersResponseDto() {
        // Default constructor for serialization/deserialization
    }

    public GetAllPlayersResponseDto(Player player) {
        this.age = player.getAge();
        this.gender = player.getGender();
        this.id = player.getId();
        this.screenName = player.getScreenName();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GetAllPlayersResponseDto that = (GetAllPlayersResponseDto) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(screenName, that.screenName) &&
            Objects.equals(gender, that.gender) &&
            Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, screenName, gender, age);
    }
}
