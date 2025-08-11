package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.UpdatePlayerRequestDto;
import com.spribe.helpers.TestDataProvider;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import org.testng.annotations.*;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class UpdatePlayerNegativeTest {

    private InterviewServiceClient interviewServiceClient;
    private String editor;
    private Queue<Long> createdPlayerIds = new ConcurrentLinkedQueue<>();

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "invalidUpdateFields", parallel = true)
    public Object[][] invalidUpdateFields() {
        return new Object[][]{
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(-1)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(0)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(16)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(61)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setAge(200)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setGender("")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setGender("wrong")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setGender(null)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setLogin("")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setLogin(null)), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setPassword("123456")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setPassword("1234567890123456")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setRole("wrong")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setScreenName("")), 400 },
            { new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer().setScreenName(null)), 400 }
        };
    }

    @Test(dataProvider = "invalidUpdateFields", enabled = false)
    @Issue("BUG-9")
    @Description("Negative update player test with invalid field values")
    public void updatePlayerInvalidFields(UpdatePlayerRequestDto invalidDto, int expectedStatusCode) {
        // Create a valid player first
        CreatePlayerRequestDto createDto = new CreatePlayerRequestDto(TestDataProvider.getTestPlayer());
        Response createResp = interviewServiceClient.createPlayer(editor, createDto);
        assertEquals(createResp.getStatusCode(), 200, "Player creation failed");
        Long createdId = createResp.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(createdId);

        // Update player with invalid field(s)
        Response updateResp = interviewServiceClient.updatePlayer(editor, createdId, invalidDto);
        assertEquals(updateResp.getStatusCode(), expectedStatusCode, "Unexpected status code for invalid update");
    }

    @Test(enabled = false)
    @Issue("BUG-10 Allowed update of non existing player")
    @Description("Update player with non-existing ID should return 404")
    public void updateNonExistingPlayerTest() {
        Long nonExistingId = 999999999L;
        UpdatePlayerRequestDto dto = new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer());

        Response resp = interviewServiceClient.updatePlayer(
            ConfigProvider.getSupervisorLogin(),
            nonExistingId,
            dto
        );

        assertEquals(resp.getStatusCode(), 404, "Updating non-existing player should return 404");
    }

    @Test
    @Description("Update player with wrong editor permissions")
    public void updatePlayerWrongEditorTest() {
        Long playerId = createPlayerAndGetId(TestDataProvider.getTestPlayer());
        UpdatePlayerRequestDto dto = new UpdatePlayerRequestDto(TestDataProvider.getTestPlayer());

        Response resp = interviewServiceClient.updatePlayer(
            "wrongEditor",
            playerId,
            dto
        );
        assertEquals(resp.getStatusCode(), 403, "Wrong editor should not be able to update");
    }

    @Test(enabled = false)
    @Issue("BUG-14 Duplicate screenNames allowed in update request")
    @Description("Update player with duplicate screenName should return error")
    public void updatePlayerWithDuplicateScreenNameTest() {
        Player player1 = TestDataProvider.getTestPlayer();
        Long id1 = createPlayerAndGetId(player1);

        Player player2 = TestDataProvider.getTestPlayer();
        Long id2 = createPlayerAndGetId(player2);

        UpdatePlayerRequestDto updateDto = new UpdatePlayerRequestDto(player2.setScreenName(player1.getScreenName()));
        Response response = interviewServiceClient.updatePlayer(editor, id2, updateDto);
        assertEquals(response.getStatusCode(), 409, "Updating with duplicate screenName should fail");
    }

    @Test
    @Description("Update player with duplicate login should return error")
    public void updatePlayerWithDuplicateLoginTest() {
        Player player1 = TestDataProvider.getTestPlayer();
        Long id1 = createPlayerAndGetId(player1);

        Player player2 = TestDataProvider.getTestPlayer();
        Long id2 = createPlayerAndGetId(player2);

        UpdatePlayerRequestDto updateDto = new UpdatePlayerRequestDto(player2.setLogin(player1.getLogin()));
        Response response = interviewServiceClient.updatePlayer(editor, id2, updateDto);
        assertEquals(response.getStatusCode(), 409, "Updating with duplicate login should fail");
    }

    private Long createPlayerAndGetId(Player player) {
        CreatePlayerRequestDto dto = new CreatePlayerRequestDto(player);
        Response response = interviewServiceClient.createPlayer(ConfigProvider.getSupervisorLogin(), dto);
        assertEquals(response.getStatusCode(), 200, "Failed to create player for update tests");
        Long id = response.as(CreatePlayerResponseDto.class).getId();
        createdPlayerIds.add(id);
        return id;
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        for (Long id : createdPlayerIds) {
            try {
                interviewServiceClient.deletePlayer(editor, id);
            } catch (Exception e) {
                System.err.println("Failed to delete player " + id + ": " + e.getMessage());
            }
        }
    }
}
