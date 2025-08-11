package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.*;
import com.spribe.helpers.AssertionHelper;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import com.spribe.helpers.TestDataProvider;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class PlayerE2ETest {
    InterviewServiceClient interviewServiceClient;
    Player player;
    String editor;

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        player = TestDataProvider.getTestPlayer();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @Test
    @Description("E2E test for Player CRUD operations")
    public void testPlayerCRUD() {
        // Create player
        CreatePlayerRequestDto playerRequest = new CreatePlayerRequestDto(player);
        Response response = interviewServiceClient.createPlayer(editor, playerRequest);

        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        CreatePlayerResponseDto createResponse = response.as(CreatePlayerResponseDto.class);
        AssertionHelper.softAssertCreatePlayer(createResponse, player);
        player.setId(createResponse.getId());

        // Update player
        TestDataProvider.updateTestPlayer(player);
        UpdatePlayerRequestDto updateRequest = new UpdatePlayerRequestDto(player);
        response = interviewServiceClient.updatePlayer(editor, player.getId(), updateRequest);

        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for update");
        UpdatePlayerResponseDto updateResponse = response.as(UpdatePlayerResponseDto.class);
        AssertionHelper.softAssertUpdatePlayer(updateResponse, player);

        //Get player
        response = interviewServiceClient.getPlayer(player.getId());
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for get player");
        GetPlayerResponseDto getResponse = response.as(GetPlayerResponseDto.class);
        AssertionHelper.softAssertGetPlayer(getResponse, player);

        //Get all players
        response = interviewServiceClient.getAllPlayers();
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for get players");
        List<GetAllPlayersResponseDto> actualPlayers =
            response.jsonPath().getList("players", GetAllPlayersResponseDto.class);
        GetAllPlayersResponseDto expectedPlayer = new GetAllPlayersResponseDto(player);
        assertTrue(actualPlayers.contains(expectedPlayer), "Player not found in the list");

        // Delete player
        response = interviewServiceClient.deletePlayer(editor, player.getId());
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for delete player");
        response = interviewServiceClient.getAllPlayers();
        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for get players");
        actualPlayers =
            response.jsonPath().getList("players", GetAllPlayersResponseDto.class);
        assertFalse(actualPlayers.contains(expectedPlayer), "Player found in the list (not deleted)");
    }

    @AfterClass
    public void tearDown() {
        if (player.getId() != null) {
            interviewServiceClient.deletePlayer(editor, player.getId());
        }
    }
}
