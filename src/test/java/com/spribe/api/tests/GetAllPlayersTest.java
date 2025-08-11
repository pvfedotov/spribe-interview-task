package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.GetAllPlayersResponseDto;
import com.spribe.dto.GetPlayerResponseDto;
import com.spribe.helpers.AssertionHelper;
import com.spribe.helpers.TestDataProvider;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class GetAllPlayersTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @Test
    @Description("Get All Players test")
    public void getAllPlayersTest() {
        // Create three different players: user, admin, and deleted
        CreatePlayerRequestDto userPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, userPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long userPlayerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(userPlayerId);
        userPlayer.setId(userPlayerId);

        CreatePlayerRequestDto adminPlayer
            = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole("admin"));
        response = interviewServiceClient.createPlayer(editor, adminPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long adminPlayerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(adminPlayerId);
        adminPlayer.setId(adminPlayerId);

        CreatePlayerRequestDto deletedPlayer
            = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        response = interviewServiceClient.createPlayer(editor, deletedPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long deletedPlayerId = response.as(CreatePlayerResponseDto.class).getId();
        deletedPlayer.setId(deletedPlayerId);
        response = interviewServiceClient.deletePlayer(editor, deletedPlayerId);
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for delete player");

        // Get all players
        response = interviewServiceClient.getAllPlayers();
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for get players");
        List<GetAllPlayersResponseDto> actualPlayers =
            response.jsonPath().getList("players", GetAllPlayersResponseDto.class);
        GetAllPlayersResponseDto expectedUser = new GetAllPlayersResponseDto(userPlayer);
        GetAllPlayersResponseDto expectedAdmin = new GetAllPlayersResponseDto(adminPlayer);
        GetAllPlayersResponseDto expectedDeleted = new GetAllPlayersResponseDto(deletedPlayer);

        // Assert that the expected players are in the list
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertTrue(actualPlayers.contains(expectedUser), "User player not found in the list");
        softAssert.assertTrue(actualPlayers.contains(expectedAdmin), "Admin player not found in the list");
        softAssert.assertFalse(actualPlayers.contains(expectedDeleted), "Deleted player found in the list");
        softAssert.assertAll();
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
