package com.spribe.dto;

import com.spribe.models.Player;

import java.util.HashMap;

public class CreatePlayerRequestDto extends Player {

    public CreatePlayerRequestDto(Player player) {
        this.age = player.getAge();
        this.gender = player.getGender();
        this.login = player.getLogin();
        this.password = player.getPassword();
        this.role = player.getRole();
        this.screenName = player.getScreenName();
    }

    public HashMap<String, Object> toMap() {
        HashMap<String, Object> map = new HashMap<>();
        map.put("age", age);
        map.put("gender", gender);
        map.put("login", login);
        if (password != null && !password.isEmpty()) {
            map.put("password", password);
        }
        map.put("role", role);
        map.put("screenName", screenName);
        return map;
    }
}
