package com.spribe.helpers;

import com.spribe.models.Player;
import org.apache.commons.lang3.RandomStringUtils;

public class TestDataProvider {
    public static Player getTestPlayer() {
        String randomSuffix = RandomStringUtils.randomAlphanumeric(6);

        return new Player()
            .setAge(25)
            .setGender("male")
            .setLogin("testUser" + randomSuffix)
            .setPassword("testPassword")
            .setRole("user")
            .setScreenName("Test User " + randomSuffix);
    }

    public static void updateTestPlayer(Player player) {
        player.setAge(50);
        player.setGender("female");
        player.setLogin(player.getLogin() + "_updated");
        player.setPassword("updatedPassword");
        player.setRole("admin");
        player.setScreenName(player.getScreenName() + " Updated");
    }
}
