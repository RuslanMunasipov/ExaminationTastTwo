package baseApi;

import io.cucumber.java.ru.Дано;
import io.cucumber.java.ru.Затем;
import io.cucumber.java.ru.И;
import io.restassured.internal.common.assertion.Assertion;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

public class baseStep {
    public String charId;
    public String mortyLoc;
    public String mortyRace;
    public String lastCharRace;
    public String lastCharLoc;
    public int lastEpisode;
    public int lastChar;

    @Дано("^Получить инфо$")
    public void gettingCharacter(String id) {
        Response gettingCharacter = given()
                .baseUri(Utils.Configuration.getConfigurationValue("baseUrl"))
                .when()
                .get("/character/" + id)
                .then()
                .extract()
                .response();
        charId = new JSONObject(gettingCharacter.getBody().asString()).get("id").toString();
        mortyLoc = new JSONObject(gettingCharacter.getBody().asString()).getJSONObject("location").get("name").toString();
        mortyRace = new JSONObject(gettingCharacter.getBody().asString()).get("species").toString();
    }

    @Затем("^Получить последний эпизод$")
    public void gettingLastEpisode() {
        Response getLastEpisode = given()
                .baseUri(Utils.Configuration.getConfigurationValue("baseUrl"))
                .when()
                .get("/character/" + charId)
                .then()
                .extract()
                .response();
        int episode = (new JSONObject(getLastEpisode.getBody().asString()).getJSONArray("episode").length() - 1);
        lastEpisode = Integer.parseInt(new JSONObject(getLastEpisode.getBody().asString()).getJSONArray("episode").get(episode).toString().replaceAll("[^0-9]", ""));
    }

    @Затем("^Получить последнего персонажа$")
    public void gettingLastCharacter() {
        Response gettingLastChar = given()
                .baseUri(Utils.Configuration.getConfigurationValue("baseUrl"))
                .when()
                .get("/episode/" + lastEpisode)
                .then()
                .extract()
                .response();
        int lastCharIndex = (new JSONObject(gettingLastChar.getBody().asString()).getJSONArray("characters").length() - 1);
        lastChar = Integer.parseInt(new JSONObject(gettingLastChar.getBody().asString()).getJSONArray("characters").get(lastCharIndex).toString().replaceAll("[^0-9]", ""));
    }

    @Затем("^Получить инфо о последнем персонаже$")
    public void gettingLastCharInfo() {
        Response lastCharInfo = given()
                .baseUri(Utils.Configuration.getConfigurationValue("baseUrl"))
                .when()
                .get("/character/" + lastChar)
                .then()
                .extract()
                .response();
        lastCharRace = new JSONObject(lastCharInfo.getBody().asString()).get("species").toString();
        lastCharLoc = new JSONObject(lastCharInfo.getBody().asString()).getJSONObject("location").get("name").toString();
    }
    @И("^Проверить совпадение локации$")
    public void locAssert(){
        Assertions.assertEquals(mortyLoc, lastCharLoc, "Не совпадает");
    }
    @И("^Проверить совпадение расы$")
    public void raceAssert(){
        Assertions.assertEquals(mortyRace, lastCharRace, "Не совпадает");
    }

    @Затем("^Отправить запрос на регрес, сравнив результаты$")
    public void sendBody() throws IOException {
        JSONObject body = new JSONObject(new String(Files.readAllBytes(Paths.get("src/test/resources/json/1.json"))));
        body.put("name", "Tomato");
        body.put("job", "Eat market");
        Response postJson = given()
                .header("Content-type", "application/json")
                .baseUri("https://reqres.in/api")
                .body(body.toString())
                .when()
                .post("/users")
                .then()
                .statusCode(201)
                .extract()
                .response();
        Assertions.assertEquals((new JSONObject(postJson.getBody().asString()).get("name")), (body.get("name")), "Fail");
        Assertions.assertEquals((new JSONObject(postJson.getBody().asString()).get("job")), (body.get("job")), "Fail");
    }
}

