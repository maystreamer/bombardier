package maystreamer.simulation;

import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public class SampleSimulation extends Simulation {
    final static String BASE_URL = "https://reqres.in/api";
    final static int USERS = 1;
    final static int RAMPUP = 1;

    public SampleSimulation() {
        HttpProtocolBuilder httpProtocol = http.baseUrl(BASE_URL)
                .header("Content-Type", "application/json");
        ScenarioBuilder scn = scenario("User Scenario")
                .exec(createUser())
                .exec(session -> {
                    System.out.println("Status Code POST: " + session.get("POST_STATUS_CODE"));
                    return session;
                })
                .exec(getUser())
                .exec(session -> {
                    System.out.println("Status Code GET: " + session.get("GET_STATUS_CODE"));
                    System.out.println("USER ID From POST: " + session.get("USER_ID"));
                    return session;
                });
        // Add the setUp block:
        {
            setUp(
                    scn.injectOpen(constantUsersPerSec(2).during(10))
            ).protocols(httpProtocol);
        }
    }

    private static HttpRequestActionBuilder createUser() {
        //builder for POST requests
        return http("Create User")
                .post("/users") // Perform a POST request
                .body(StringBody("{\"name\":\"morpheus\",\"job\":\"leader\"}")) // Request body
                .check(status().is(201).saveAs("POST_STATUS_CODE"))
                .check(
                        jsonPath("$.id").saveAs("USER_ID")
                );
    }

    private static HttpRequestActionBuilder getUser() {
        //builder for POST requests
        return http("Get User")
                .get("/users/${USER_ID}") // Perform a POST request
                .check(status().is(200).saveAs("GET_STATUS_CODE"));
    }
}