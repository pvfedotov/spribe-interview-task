package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.GetPlayerResponseDto;
import com.spribe.helpers.AssertionHelper;
import com.spribe.helpers.TestDataProvider;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class GetPlayerTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @Test
    @Description("Get User Player test")
    public void getUserPlayerTest() {
        CreatePlayerRequestDto originalPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long playerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(playerId);
        originalPlayer.setId(playerId);

        response = interviewServiceClient.getPlayer(playerId);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        GetPlayerResponseDto getResponse = response.as(GetPlayerResponseDto.class);
        AssertionHelper.softAssertGetPlayer(getResponse, originalPlayer);
    }

    @Test
    @Description("Get Admin Player test")
    public void getAdminPlayerTest() {
        CreatePlayerRequestDto originalPlayer
            = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole("admin"));
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long playerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(playerId);
        originalPlayer.setId(playerId);

        response = interviewServiceClient.getPlayer(playerId);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        GetPlayerResponseDto getResponse = response.as(GetPlayerResponseDto.class);
        AssertionHelper.softAssertGetPlayer(getResponse, originalPlayer);
    }

    @Test
    @Description("Get Supervisor Player test")
    public void getSupervisorPlayerTest() {
        Response response = interviewServiceClient.getPlayer(ConfigProvider.getSupervisorId());
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        GetPlayerResponseDto getResponse = response.as(GetPlayerResponseDto.class);
        AssertionHelper.softAssertGetPlayerSupervisor(getResponse);
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
