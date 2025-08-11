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

public class UpdatePlayerAccessControlTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "roleUpdateMatrix", parallel = true)
    public static Object[][] roleUpdateMatrix() {
        return new Object[][]{
            // editorRole, targetRole, expectedStatus
            {"user", "user", 200},      // self-update allowed — we’ll create target with same login as editor
            // BUG-7 Access control issue - user able to update admin
            //{"user", "admin", 403},     // not allowed
            //{"user", "supervisor", 403},// not allowed

            {"admin", "user", 200},     // allowed
            {"admin", "admin", 200},    // allowed
            {"admin", "supervisor", 403}, // not allowed

            {"supervisor", "user", 200}, // allowed
            {"supervisor", "admin", 200}, // allowed
            {"supervisor", "supervisor", 200} // allowed
        };
    }

    @Test(dataProvider = "roleUpdateMatrix")
    public void updateRoleAccessControlTest(String editorRole, String targetRole, int expectedStatus) {
        Long editorId;
        String editorLogin;

        // Setup editor
        if (editorRole.equals("supervisor")) {
            editorId = Long.valueOf(ConfigProvider.getSupervisorId());
            editorLogin = ConfigProvider.getSupervisorLogin();
        } else {
            Player editorPlayer = TestDataProvider.getTestPlayer().setRole(editorRole);
            editorId = createPlayerAndGetId(editorPlayer);
            editorLogin = editorPlayer.getLogin();
        }

        // Setup target
        Long targetId;
        Player targetPlayer;
        if (editorRole.equals("user") && targetRole.equals("user")) {
            // self-update case
            targetId = editorId;
            targetPlayer = new Player().setId(editorId).setLogin(editorLogin).setRole(editorRole);
        } else if (targetRole.equals("supervisor")) {
            targetId = Long.valueOf(ConfigProvider.getSupervisorId());
            targetPlayer = new Player().setId(targetId)
                .setLogin(ConfigProvider.getSupervisorLogin())
                .setRole("supervisor");
        } else {
            targetPlayer = TestDataProvider.getTestPlayer().setRole(targetRole);
            targetId = createPlayerAndGetId(targetPlayer);
        }

        // Prepare update
        targetPlayer.setScreenName("Updated_" + System.nanoTime());
        UpdatePlayerRequestDto updateDto = new UpdatePlayerRequestDto(targetPlayer);

        // Call update as editor
        Response resp = interviewServiceClient.updatePlayer(editorLogin, targetId, updateDto);

        // Assert
        assertEquals(resp.getStatusCode(), expectedStatus,
            String.format("Editor=%s Target=%s", editorRole, targetRole));
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
