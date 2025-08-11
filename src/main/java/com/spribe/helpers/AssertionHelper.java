package com.spribe.helpers;

import com.spribe.dto.CreatePlayerResponseDto;
import com.spribe.dto.GetPlayerResponseDto;
import com.spribe.dto.UpdatePlayerResponseDto;
import com.spribe.models.Player;
import com.spribe.utils.ConfigProvider;
import org.testng.asserts.SoftAssert;

public class AssertionHelper {
    public static void softAssertCreatePlayer(CreatePlayerResponseDto actual, Player expected) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertNotNull(actual.getId(), "ID should not be null");
        softAssert.assertEquals(actual.getLogin(), expected.getLogin(), "Login mismatch");
        //Uncomment the following lines after bug fix:
        //BUG-1 Nulls as Password, ScreenName, Gender, Age, Role values in Create Player response
        //softAssert.assertEquals(actual.getPassword(), expected.getPassword(), "Password mismatch");
        //softAssert.assertEquals(actual.getScreenName(), expected.getScreenName(), "Screen name mismatch");
        //softAssert.assertEquals(actual.getGender(), expected.getGender(), "Gender mismatch");
        //softAssert.assertEquals(actual.getAge(), expected.getAge(), "Age mismatch");
        //softAssert.assertEquals(actual.getRole(), expected.getRole(), "Role mismatch");
        softAssert.assertAll();
    }

    public static void softAssertUpdatePlayer(UpdatePlayerResponseDto actual, Player expected) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actual.getAge(), expected.getAge(), "Age mismatch");
        softAssert.assertEquals(actual.getGender(), expected.getGender(), "Gender mismatch");
        softAssert.assertEquals(actual.getId(), expected.getId(), "Id mismatch");
        softAssert.assertEquals(actual.getLogin(), expected.getLogin(), "Login mismatch");
        //Uncomment the following lines after bug fix:
        //BUG-2 Player Update request doesn't update role value
        //softAssert.assertEquals(actual.getRole(), expected.getRole(), "Role mismatch");
        softAssert.assertEquals(actual.getScreenName(), expected.getScreenName(), "Screen name mismatch");
        softAssert.assertAll();
    }

    public static void softAssertGetPlayer(GetPlayerResponseDto actual, Player expected) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actual.getId(), expected.getId(), "ID mismatch");
        softAssert.assertEquals(actual.getLogin(), expected.getLogin(), "Login mismatch");
        softAssert.assertEquals(actual.getPassword(), expected.getPassword(), "Password mismatch");
        softAssert.assertEquals(actual.getScreenName(), expected.getScreenName(), "Screen name mismatch");
        softAssert.assertEquals(actual.getGender(), expected.getGender(), "Gender mismatch");
        softAssert.assertEquals(actual.getAge(), expected.getAge(), "Age mismatch");
        //Uncomment the following lines after bug fix:
        //BUG-2 Player Update request doesn't update role value
        //softAssert.assertEquals(actual.getRole(), expected.getRole(), "Role mismatch");
        softAssert.assertAll();
    }

    public static void softAssertGetPlayerSupervisor(GetPlayerResponseDto actual) {
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actual.getId().toString(), ConfigProvider.getSupervisorId(), "ID mismatch");
        softAssert.assertEquals(actual.getLogin(),  ConfigProvider.getSupervisorLogin(), "Login mismatch");
        softAssert.assertNotNull(actual.getScreenName(), "ScreenName should not be null");
        softAssert.assertNotNull(actual.getGender(), "Gender should not be null");
        softAssert.assertNotNull(actual.getAge(), "Age should not be null");
        softAssert.assertEquals(actual.getRole(), "supervisor", "Role mismatch");
        softAssert.assertAll();
    }
}
