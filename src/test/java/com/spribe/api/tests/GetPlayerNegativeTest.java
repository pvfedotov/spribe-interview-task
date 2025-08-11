package com.spribe.api.tests;

import com.spribe.clients.InterviewServiceClient;
import com.spribe.utils.ConfigProvider;
import io.qameta.allure.Description;
import io.qameta.allure.Issue;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import static org.testng.Assert.assertEquals;

public class GetPlayerNegativeTest {
    InterviewServiceClient interviewServiceClient;
    String editor;

    @BeforeClass
    public void setup() {
        interviewServiceClient = new InterviewServiceClient();
        editor = ConfigProvider.getSupervisorLogin();
    }

    @DataProvider(name = "idsToGet", parallel = true)
    public static Object[][] idsToGet() {
        return new Object[][]{
            { null, 400 },
            { 0, 400 },
            { -1, 400 },
            { 999999999, 404 }, // Assuming this ID does not exist
            { "wrong", 400 }, // Non-numeric ID
        };
    }

    @Test(dataProvider = "idsToGet", enabled = false)
    @Issue("BUG-3")
    @Description("Get Player Negative test")
    public void getPlayerNegativeTest(Object id, int expectedStatusCode) {
        Response response = interviewServiceClient.getPlayer(id);
        assertEquals(response.getStatusCode(), expectedStatusCode, "Wrong status code");
    }
}
