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

import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class CreatePlayerNegativeTest {

    private InterviewServiceClient interviewServiceClient;
    private String editor;
    private final Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "playerAccounts", parallel = true)
    public static Object[][] playerAccounts() {
        return new Object[][]{
            // Age tests
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(-1)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(0)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(16)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(61)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(null)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer()) {
                @Override
                public HashMap<String, Object> toMap() {
                    HashMap<String, Object> map = super.toMap();
                    map.remove("age");
                    return map;
                }
            }},
            // Gender tests
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setGender("")) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setGender(null)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer()) {
                @Override
                public HashMap<String, Object> toMap() {
                    HashMap<String, Object> map = super.toMap();
                    map.remove("gender");
                    return map;
                }
            }},
            // Login tests
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setLogin("")) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setLogin(null)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer()) {
                @Override
                public HashMap<String, Object> toMap() {
                    HashMap<String, Object> map = super.toMap();
                    map.remove("login");
                    return map;
                }
            }},
            // Role tests
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole("")) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole(null)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole("wrong")) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer()) {
                @Override
                public HashMap<String, Object> toMap() {
                    HashMap<String, Object> map = super.toMap();
                    map.remove("role");
                    return map;
                }
            }},
            // Screen name tests
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setScreenName("")) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setScreenName(null)) },
            { new CreatePlayerRequestDto(TestDataProvider.getTestPlayer()) {
                @Override
                public HashMap<String, Object> toMap() {
                    HashMap<String, Object> map = super.toMap();
                    map.remove("screenName");
                    return map;
                }
            }},
        };
    }

    @DataProvider(name = "editors", parallel = true)
    public static Object[][] editors() {
        return new Object[][]{
            { "", 404 },
            { "wrong", 403 }
        };
    }

    @Test(dataProvider = "playerAccounts")
    @Description("Create Player negative test - invalid or missing fields")
    public void createPlayerTest(CreatePlayerRequestDto playerDto) {
        Response response = interviewServiceClient.createPlayer(editor, playerDto);
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for create player");
    }

    @Test(dataProvider = "editors")
    @Description("Create Player with invalid editor")
    public void createPlayerWrongEditorTest(String wrongEditor, int expectedStatusCode) {
        CreatePlayerRequestDto playerDto = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(wrongEditor, playerDto);
        assertEquals(response.getStatusCode(), expectedStatusCode, "Wrong status code");
    }

    @Test(enabled = false)
    @Issue("BUG-4")
    @Description("Create Player with existing login should fail")
    public void createPlayerExistingLoginTest() {
        CreatePlayerRequestDto originalPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        createdPlayerIds.add(response.as(CreatePlayerResponseDto.class).getId());

        CreatePlayerRequestDto duplicateLogin =
            new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setLogin(originalPlayer.getLogin()));
        response = interviewServiceClient.createPlayer(editor, duplicateLogin);
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for duplicate login");
    }

    @Test(enabled = false)
    @Issue("BUG-5")
    @Description("Create Player with existing screen name should fail")
    public void createPlayerExistingScreenNameTest() {
        CreatePlayerRequestDto originalPlayer = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response response = interviewServiceClient.createPlayer(editor, originalPlayer);
        assertEquals(response.getStatusCode(), 200, "Expected status code 200");
        createdPlayerIds.add(response.as(CreatePlayerResponseDto.class).getId());

        CreatePlayerRequestDto duplicateScreenName =
            new CreatePlayerRequestDto(TestDataProvider.getTestPlayer().setScreenName(originalPlayer.getScreenName()));
        response = interviewServiceClient.createPlayer(editor, duplicateScreenName);
        assertEquals(response.getStatusCode(), 400, "Expected status code 400 for duplicate screen name");
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
