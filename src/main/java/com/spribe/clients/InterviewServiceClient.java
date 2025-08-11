package com.spribe.clients;

import com.spribe.dto.CreatePlayerRequestDto;
import com.spribe.dto.UpdatePlayerRequestDto;
import com.spribe.utils.ConfigProvider;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class InterviewServiceClient {
    private RequestSpecification requestSpecification;

    public InterviewServiceClient() {
        requestSpecification = new RequestSpecBuilder()
            .addHeader("content-type", "application/json;charset=UTF-8")
            .setBaseUri(ConfigProvider.getBaseUrl())
            .log(LogDetail.ALL)
            .build();
    }

    public Response createPlayer(String editor, CreatePlayerRequestDto requestParams) {
        return RestAssured.given()
            .spec(requestSpecification)
            .basePath("/player/create/{editor}")
            .pathParam("editor", editor)
            .queryParams(requestParams.toMap())
            .get();
    }

    public Response updatePlayer(String editor, Object id, UpdatePlayerRequestDto request) {
        return RestAssured.given()
            .spec(requestSpecification)
            .basePath("/player/update/{editor}/{id}")
            .pathParam("editor", editor)
            .pathParam("id", id)
            .body(request)
            .patch();
    }

    public Response getPlayer(Object id) {
        Map<String, Object> body = new HashMap<>();
        body.put("playerId", id);

        return RestAssured.given()
            .spec(requestSpecification)
            .basePath("/player/get")
            .body(body)
            .post();
    }

    public Response getAllPlayers() {
        return RestAssured.given()
            .spec(requestSpecification)
            .basePath("/player/get/all")
            .get();
    }

    public Response deletePlayer(String editor, Object id) {
        Map<String, Object> body = new HashMap<>();
        body.put("playerId", id);

        return RestAssured.given()
            .spec(requestSpecification)
            .basePath("/player/delete/{editor}")
            .pathParam("editor", editor)
            .body(body)
            .delete();
    }
}
