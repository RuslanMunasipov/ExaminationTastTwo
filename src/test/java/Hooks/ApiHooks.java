package Hooks;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class ApiHooks{
    @BeforeAll
    public static void before () {
        RestAssured.filters(new AllureRestAssured());
    }

}
