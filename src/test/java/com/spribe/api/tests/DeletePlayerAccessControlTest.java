package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.UpdatePlayerRequestDto;
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

public class DeletePlayerAccessControlTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "roleDeleteMatrix", parallel = true)
    public static Object[][] roleDeleteMatrix() {
        return new Object[][]{
            // editorRole, targetRole, expectedStatus
            {"user", "user", 204},     // delete self
            // BUG-8 Access control issue - user able to delete admin
            //{"user", "admin", 403},
            //not safe to try
            //{"user", "supervisor", 403},

            {"admin", "user", 204},
            {"admin", "admin", 204},
            //not safe to try
            //{"admin", "supervisor", 403},

            {"supervisor", "user", 204},
            {"supervisor", "admin", 204},
            //not safe to try
            //{"supervisor", "supervisor", 403}
        };
    }

    @Test(dataProvider = "roleDeleteMatrix")
    public void deletePlayerAccessControlTest(String editorRole, String targetRole, int expectedStatus) {
        String editorLogin;
        Long editorId = null;

        // Setup editor
        if (editorRole.equals("supervisor")) {
            editorLogin = ConfigProvider.getSupervisorLogin();
            editorId = Long.valueOf(ConfigProvider.getSupervisorId());
        } else {
            Player editorPlayer = TestDataProvider.getTestPlayer().setRole(editorRole);
            editorId = createPlayerAndGetId(editorPlayer);
            editorLogin = editorPlayer.getLogin();
        }

        // Setup target player
        Long targetId;
        if (editorRole.equals("user") && targetRole.equals("user")) {
            // "user" can delete only themselves
            targetId = editorId;
        } else {
            Player targetPlayer = TestDataProvider.getTestPlayer().setRole(targetRole);
            targetId = createPlayerAndGetId(targetPlayer);
        }

        // Call delete
        Response resp = interviewServiceClient.deletePlayer(editorLogin, targetId);

        // Assert
        assertEquals(resp.getStatusCode(), expectedStatus,
            String.format("Editor=%s Target=%s", editorRole, targetRole));

        // Cleanup — only if the player was deleted successfully
        if (resp.getStatusCode() != 200 && !targetId.equals(editorId)) {
            createdPlayerIds.add(targetId);
        }
    }

    private Long createPlayerAndGetId(Player player) {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto(player);
        Response resp = interviewServiceClient.createPlayer(ConfigProvider.getSupervisorLogin(), dto);
        assertEquals(resp.getStatusCode(), 200, "Player creation failed");
        Long id = resp.as(CreatePlayerResponseDto.class).getId();
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
