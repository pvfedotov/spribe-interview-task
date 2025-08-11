package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.helpers.TestDataProvider;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

public class DeletePlayerNegativeTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "idsToDelete", parallel = true)
    public static Object[][] idsToDelete() {
        return new Object[][]{
            { null, 403 },
            { 0, 403 },
            { -1, 403 },
            { 999999999, 403 }, // Assuming this ID does not exist
            { "wrong", 400 }, // Non-numeric ID
        };
    }

    @DataProvider(name = "editors", parallel = true)
    public static Object[][] editors() {
        return new Object[][]{
            { "", 404 },
            { "wrong", 403 }
        };
    }

    @Test(dataProvider = "idsToDelete")
    @Description("Delete Player Negative test")
    public void deletePlayerNegativeTest(Object id, int expectedStatusCode) {
        Response response = interviewServiceClient.deletePlayer(editor, id);
        assertEquals(response.getStatusCode(), expectedStatusCode, "Wrong status code");
    }

    @Test(dataProvider = "editors", enabled = false)
    @Issue("BUG-6")
    @Description("Delete Player Negative test")
    public void deletePlayerEditorNegativeTest(String wrongEditor, int expectedStatusCode) {
        CreatePlayerRequestDto originalPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long playerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(playerId);

        response = interviewServiceClient.deletePlayer(wrongEditor, playerId);
        assertEquals(response.getStatusCode(), expectedStatusCode, "Wrong status code");
    }

    @Test(enabled = false)
    @Issue("BUG-5")
    @Description("Delete already deleted Player test")
    public void deleteAlreadyDeletedPlayerTest() {
        CreatePlayerRequestDto originalPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        Long playerId = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(playerId);

        response = interviewServiceClient.deletePlayer(editor, playerId);
        assertEquals(response.getStatusCode(), 204, "Expected status code 204 for delete player");

        response = interviewServiceClient.deletePlayer(editor, playerId);
        assertEquals(response.getStatusCode(), 404, "Expected status code 404 for missing player");
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
