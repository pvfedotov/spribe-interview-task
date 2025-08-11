package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.helpers.AssertionHelper;
import com.spribe.helpers.TestDataProvider;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class CreatePlayerTest {
    InterviewServiceClient interviewServiceClient;
    String editor;
    Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "playerAccounts", parallel = true)
    public static Object[][] playerAccounts() {
        return new Object[][]{
            //Restriction on player age below 17 under 60?
            //{ TestDataProvider.getTestPlayer().setAge(0).setGender("male").setRole("user") },
            //{ TestDataProvider.getTestPlayer().setAge(99).setGender("female").setRole("admin") },
            { TestDataProvider.getTestPlayer().setAge(17).setGender("male").setRole("user") },
            { TestDataProvider.getTestPlayer().setAge(60).setGender("female").setRole("admin") },
            //Supervisor unable to create supervisors?
            //{ TestDataProvider.getTestPlayer().setAge(50).setGender("non-binary").setRole("supervisor") },
            { TestDataProvider.getTestPlayer().setAge(50).setGender("non-binary").setRole("admin") },
            { TestDataProvider.getTestPlayer().setAge(20).setGender("random").setRole("user").setPassword(null) },
            { TestDataProvider.getTestPlayer().setGender(" !\"#$%&'()*+,-.\\/:;<=>?@[]^_`{|}~") },
            { TestDataProvider.getTestPlayer().setLogin(" !\"#$%&'()*+,-.\\/:;<=>?@[]^_`{|}~") },
            { TestDataProvider.getTestPlayer().setScreenName(" !\"#$%&'()*+,-.\\/:;<=>?@[]^_`{|}~") },
            { TestDataProvider.getTestPlayer().setPassword(" !\"#$%&'()*+,-.\\/:;<=>?@[]^_`{|}~") }
        };
    }

    @Test(dataProvider = "playerAccounts")
    @Description("Create Player test")
    public void createPlayerTest(Player player) {
        CreatePlayerRequestDto playerRequest = new CreatePlayerRequestDto(player);
        Response response = interviewServiceClient.createPlayer(editor, playerRequest);

        assertEquals(response.getStatusCode(), 200, "Expected status code 200 for create player");

        CreatePlayerResponseDto createResponse = response.as(CreatePlayerResponseDto.class);
        AssertionHelper.softAssertCreatePlayer(createResponse, player);

        // Save the created player ID for later deletion
        if (createResponse.getId() != null) {
            createdPlayerIds.add(createResponse.getId());
        }
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
