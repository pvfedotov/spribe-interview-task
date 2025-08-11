package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.UpdatePlayerRequestDto;
import com.spribe.dto.UpdatePlayerResponseDto;
import com.spribe.helpers.AssertionHelper;
import com.spribe.helpers.TestDataProvider;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class UpdatePlayerTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "updateFields", parallel = true)
    public Object[][] updateFields() {
        return new Object[][]{
            { new Player().setAge(40) },
            { new Player().setGender("female") },
            { new Player().setLogin("testUser" + RandomStringUtils.randomAlphanumeric(6)) },
            { new Player().setPassword("newPass123") },
            { new Player().setRole("admin") },
            { new Player().setScreenName("Test User " + RandomStringUtils.randomAlphanumeric(6)) },
            { new Player()
                .setAge(45)
                .setGender("female")
                .setLogin("testUser" + RandomStringUtils.randomAlphanumeric(6))
                .setPassword("allFieldsPass")
                .setRole("user")
                .setScreenName("Test User " + RandomStringUtils.randomAlphanumeric(6)) }
        };
    }

    @Test(dataProvider = "updateFields")
    public void updatePlayerTest(Player updates) {
        // Create player
        Player basePlayer = TestDataProvider.getTestPlayer();
        Long playerId = interviewServiceClient.createPlayer(editor, new CreatePlayerRequestDto(basePlayer))
            .as(CreatePlayerResponseDto.class)
            .getId();

        createdPlayerIds.add(playerId);

        // Merge updates into a DTO
        Player updatedPlayer = new Player()
            .setId(playerId)
            .setAge(updates.getAge() != null ? updates.getAge() : basePlayer.getAge())
            .setGender(updates.getGender() != null ? updates.getGender() : basePlayer.getGender())
            .setLogin(updates.getLogin() != null ? updates.getLogin() : basePlayer.getLogin())
            .setPassword(updates.getPassword() != null ? updates.getPassword() : basePlayer.getPassword())
            .setRole(updates.getRole() != null ? updates.getRole() : basePlayer.getRole())
            .setScreenName(updates.getScreenName() != null ? updates.getScreenName() : basePlayer.getScreenName());

        UpdatePlayerRequestDto updateRequest = new UpdatePlayerRequestDto(updatedPlayer);

        // Send update request
        Response updateResponse = interviewServiceClient.updatePlayer(editor, playerId, updateRequest);
        assertEquals(updateResponse.getStatusCode(), 200, "Update request failed");

        // Verify the player was updated
        AssertionHelper.softAssertUpdatePlayer(updateResponse.as(UpdatePlayerResponseDto.class), updatedPlayer);
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        for (Long playerId : createdPlayerIds) {
            try {
                interviewServiceClient.deletePlayer(editor, playerId);
            } catch (Exception e) {
                System.err.println("Failed to delete player with ID " + playerId + ": " + e.getMessage());
            }
        }
    }
}
