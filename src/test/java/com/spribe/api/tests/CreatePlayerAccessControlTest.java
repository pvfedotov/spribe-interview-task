package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.helpers.TestDataProvider;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class CreatePlayerAccessControlTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "roleCreateMatrix", parallel = true)
    public static Object[][] roleCreateMatrix() {
        return new Object[][]{
            // editorRole, newPlayerRole, expectedStatus
            {"user", "user", 403},
            {"user", "admin", 403},
            {"user", "supervisor", 400},

            {"admin", "user", 200},
            {"admin", "admin", 200},
            {"admin", "supervisor", 400},

            {"supervisor", "user", 200},
            {"supervisor", "admin", 200},
            {"supervisor", "supervisor", 400}
        };
    }

    @Test(dataProvider = "roleCreateMatrix")
    public void createPlayerAccessControlTest(String editorRole, String newPlayerRole, int expectedStatus) {
        String editorLogin;
        Long editorId;

        // Setup editor
        if (editorRole.equals("supervisor")) {
            editorLogin = ConfigProvider.getSupervisorLogin();
            editorId = Long.valueOf(ConfigProvider.getSupervisorId());
        } else {
            Player editorPlayer = TestDataProvider.getTestPlayer().setRole(editorRole);
            editorId = createPlayerAndGetId(editorPlayer);
            editorLogin = editorPlayer.getLogin();
        }

        // Setup player to be created
        Player newPlayer;
        if (editorRole.equals("user") && newPlayerRole.equals("user")) {
            newPlayer = new Player()
                .setId(editorId)
                .setLogin(editorLogin)
                .setPassword("testPassword")
                .setScreenName("Self_Create_" + System.nanoTime())
                .setRole(editorRole)
                .setGender("male")
                .setAge(25);
        } else {
            newPlayer = TestDataProvider.getTestPlayer().setRole(newPlayerRole);
        }

        // Create request
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto(newPlayer);

        // Call create as editor
        Response resp = interviewServiceClient.createPlayer(editorLogin, dto);

        // Assert
        assertEquals(resp.getStatusCode(), expectedStatus,
            String.format("Editor=%s NewPlayer=%s", editorRole, newPlayerRole));

        // Track created players for cleanup if created successfully
        if (resp.getStatusCode() == 200) {
            Long newId = resp.as(CreatePlayerResponseDto.class).getId();
            createdPlayerIds.add(newId);
        }
    }

    private Long createPlayerAndGetId(Player player) {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto(player);
        Response response = interviewServiceClient.createPlayer(ConfigProvider.getSupervisorLogin(), dto);
        assertEquals(response.getStatusCode(), 200, "Player creation failed");
        Long id = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(id);
        return id;
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
