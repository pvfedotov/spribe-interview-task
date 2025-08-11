package com.spribe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.spribe.models.Player;

import java.util.HashMap;

public class UpdatePlayerRequestDto extends Player {

    public UpdatePlayerRequestDto(Player player) {
        this.age = player.getAge();
        this.gender = player.getGender();
        this.login = player.getLogin();
        this.password = player.getPassword();
        this.role = player.getRole();
        this.screenName = player.getScreenName();
    }

    @Override
    @JsonIgnore
    public Long getId() {
        return super.getId();
    }
}
