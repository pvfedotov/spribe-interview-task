package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.GetAllPlayersResponseDto;
import com.spribe.helpers.TestDataProvider;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DeletePlayerTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @Test
    @Description("Delete player test")
    public void deletePlayerTest() {
        // Create a player to delete
        CreatePlayerRequestDto targetPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, targetPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long playerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(playerId);

        //delete the created player
        response = interviewServiceClient.deletePlayer(editor, playerId);
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for delete player");

        // Verify player deletion
        response = interviewServiceClient.getAllPlayers();
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for get players");
        List<GetAllPlayersResponseDto> actualPlayers =
            response.jsonPath().getList("players", GetAllPlayersResponseDto.class);
        assertFalse(actualPlayers.contains(targetPlayer), "Player found in the list (not deleted)");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        for (Long playerId : createdPlayerIds) {
            try {
                interviewServiceClient.deletePlayer(editor, playerId);
            } catch (Exception e) {
                System.out.println("Failed to delete player with ID " + playerId + ": " + e.getMessage());
            }
        }
    }
}
