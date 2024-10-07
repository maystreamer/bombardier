package maystreamer.simulation;

import ch.qos.logback.core.util.StringUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.gatling.javaapi.core.ChainBuilder;
import io.gatling.javaapi.core.PauseType;
import io.gatling.javaapi.core.ScenarioBuilder;
import io.gatling.javaapi.core.Simulation;
import io.gatling.javaapi.http.HttpProtocolBuilder;
import io.gatling.javaapi.http.HttpRequestActionBuilder;
import maystreamer.Util.JsonUtils;
import maystreamer.config.Configuration;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.http;
import static io.gatling.javaapi.http.HttpDsl.status;

public abstract class AbstractSimulation extends Simulation {
    //protected JsonObject loadConfig = null;
    protected JsonObject injectionProfile = null;
    protected HttpProtocolBuilder httpProtocolBuilder = null;
    //protected List<ScenarioBuilder> scenarioBuilders = new LinkedList<>();
    protected ScenarioBuilder scenarioBuilder = null;
    protected ChainBuilder chainBuilder = null;

    protected Double percentile;
    protected Double responseTimeLessThan;
    protected Double successRatioPercentageHigherThan;

    private static final String CONFIG_RESOURCE_NAME = "configuration.json";
    private static final String API_RESOURCE_NAME = "apis/apis.json";

    public AbstractSimulation() {
        try {
            //set test profile
            JsonObject json = Configuration.load(CONFIG_RESOURCE_NAME);
            JsonObject testConfig = json.getAsJsonObject("load_config");
            buildInjectionProfile(testConfig);
            buildAcceptanceCriteria(testConfig);

            //set APIs and Scenarios
            json = Configuration.load(API_RESOURCE_NAME);
            final JsonObject apiContext = json.getAsJsonObject("api_context");
            this.httpProtocolBuilder = buildHttpProtocol(apiContext);
            //this.scenarioBuilders = buildScenario(apiContext);
            //this.chainBuilder = buildChain(apiContext);
            buildScenario(apiContext);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void buildInjectionProfile(final JsonObject testConfig) {
        this.injectionProfile = testConfig.getAsJsonObject("injection_profile");
    }

    private void buildAcceptanceCriteria(final JsonObject testConfig) {
        final JsonObject acceptanceCriteria = testConfig.getAsJsonObject("acceptance_criteria");
        percentile = Double.parseDouble(acceptanceCriteria.get("percentile").getAsString());
        responseTimeLessThan = (Double.parseDouble((acceptanceCriteria.get("response_time_less_than").getAsString()))) * 1000;
        successRatioPercentageHigherThan = Double.parseDouble(acceptanceCriteria.get("success_ratio_percentage_higher_than").getAsString());
    }

    private static HttpProtocolBuilder buildHttpProtocol(final JsonObject apiContext) {
        final JsonObject apiConfig = apiContext.getAsJsonObject("api_config");
        return http.baseUrl(apiConfig.get("base_url").getAsString())
                .headers(buildHeaders(apiConfig))
                .maxConnectionsPerHost(10)
                .userAgentHeader("MayStreamer Gatling Performance Test");
    }

    private static Map<String, String> buildHeaders(final JsonObject apiConfig) {
        final JsonObject commonHeaders = apiConfig.getAsJsonObject("common_headers");
        Map<String, String> attributes = new HashMap<>();
        Set<Map.Entry<String, JsonElement>> entrySet = commonHeaders.entrySet();
        for (Map.Entry<String, JsonElement> entry : entrySet) {
            attributes.put(entry.getKey(), commonHeaders.get(entry.getKey()).getAsString());
        }
        return attributes;
    }

    private void buildScenario(final JsonObject apiContext) {
        JsonArray apiScenarios = apiContext.getAsJsonArray("api_scenarios");
        for (JsonElement element : apiScenarios) {
            JsonObject scenario = element.getAsJsonObject();
            String scenarioName = scenario.get("scenario").getAsString().trim();
            String scenarioToRun = System.getProperty("scenario.toRun");
            scenarioToRun = !StringUtil.isNullOrEmpty(scenarioToRun) ? scenarioToRun.trim().toUpperCase() : "";
            if(!scenarioToRun.equalsIgnoreCase(scenarioName.trim().toUpperCase())){
               continue;
            }
            this.scenarioBuilder = scenario(scenarioName);
            JsonArray chainReq = scenario.getAsJsonArray("requests");
            for (JsonElement ch : chainReq) {
                JsonObject chainConfig = ch.getAsJsonObject();
                String apiName = chainConfig.get("name").getAsString().trim();
                JsonObject api = chainConfig.get("api").getAsJsonObject();
                String method = api.get("method").getAsString().trim().toUpperCase();
                final int pause = chainConfig.get("pause").getAsInt();
                final String payload_type = JsonUtils.value(chainConfig, "payload_type", "");
                // Create the request based on method
                switch (method) {
                    case "GET":
                        this.scenarioBuilder = this.scenarioBuilder.exec(getRequest(api, apiName))
                                .exec(session -> {
                                    System.out.println("Status Code Get " + api + ": " + session.get("GET_STATUS_CODE"));
                                    return session;
                                });
                        break;
                    case "POST":
                        this.scenarioBuilder = this.scenarioBuilder.exec(postRequest(api, apiName, payload_type))
                                .exec(session -> {
                                    System.out.println("Status Code Post " + api + ": " + session.get("POST_STATUS_CODE"));
                                    return session;
                                });
                        break;
                }
                String pauseType = chainConfig.get("pause_type").getAsString().trim().toUpperCase();
                this.scenarioBuilder = this.scenarioBuilder.pause(Duration.ofSeconds(pause), getPauseType(pauseType));
            }
        }
    }

    private List<ScenarioBuilder> buildScenario(final JsonObject apiContext, final int data) {
        JsonArray apiScenarios = apiContext.getAsJsonArray("api_scenarios");
        ChainBuilder chainBuilder = exec(session -> session);
        for (JsonElement element : apiScenarios) {
            JsonObject scenario = element.getAsJsonObject();
            String scenarioName = scenario.get("scenario").getAsString().trim();
            System.out.println(scenarioName);
            JsonArray chainReq = scenario.getAsJsonArray("requests");
            for (JsonElement ch : chainReq) {
                JsonObject chainConfig = ch.getAsJsonObject();
                String apiName = chainConfig.get("name").getAsString().trim();
                JsonObject api = chainConfig.get("api").getAsJsonObject();
                String method = api.get("method").getAsString().trim().toUpperCase();
                final int pause = chainConfig.get("pause").getAsInt();
                final String payload_type = JsonUtils.value(chainConfig, "payload_type", "");
                switch (method) {
                    case "GET":
                        chainBuilder = chainBuilder.exec(getRequest(api, apiName))
                                .exec(session -> {
                                    System.out.println("Status Code Get: " + session.get("GET_STATUS_CODE"));
                                    return session;
                                });
                        break;
                    case "POST":
                        chainBuilder = chainBuilder.exec(postRequest(api, apiName, payload_type))
                                .exec(session -> {
                                    System.out.println("Status Code Post: " + session.get("POST_STATUS_CODE"));
                                    return session;
                                });
                        break;
                }
                //addPause(chainBuilder, chainConfig);
            }
            //this.scenarioBuilders.add(scenario(scenarioName).exec(chainBuilder));
        }
        //return this.scenarioBuilders;
        return null;
    }


    private ScenarioBuilder getScenario(final String scenarioName) {
        ScenarioBuilder scn = scenario(scenarioName);
        return scn;
    }

    private static HttpRequestActionBuilder getRequest(final JsonObject api, final String name) {
        //builder for GET requests
        return http(name)
                .get(api.get("path").getAsString()) // Perform a GET request
                .check(status().is(Integer.parseInt(api.get("status").getAsString())).saveAs("GET_STATUS_CODE"));
    }

    private static HttpRequestActionBuilder postRequest(final JsonObject api, final String name, final String payloadType) {
        //builder for POST requests
        return http(name)
                .post(api.get("path").getAsString()) // Perform a POST request
                .body(payload(api, payloadType)) // Request body
                .check(status().is(Integer.parseInt(api.get("status").getAsString())).saveAs("POST_STATUS_CODE"));
    }

    private static PauseType getPauseType(final String pauseType) {
        switch (pauseType) {
            case "DISABLED":
                return PauseType.Disabled;
            case "EXPONENTIAL":
                return PauseType.Exponential;
        }
        return PauseType.Constant;
    }

    private ScenarioBuilder deleteScenario(final JsonObject endpoint, final String scenarioName) {
        // Scenario for DELETE requests
        ScenarioBuilder deleteScn = scenario("DELETE Scenario")
                .exec(http("DELETE request")
                        .delete("/") // Perform a DELETE request
                        .check(status().is(204))); // Check for a 204 No Content response

        // Scenario for PATCH requests
        ScenarioBuilder patchScn = scenario("PATCH Scenario")
                .exec(http("PATCH request")
                        .patch("/") // Perform a PATCH request
                        .body(StringBody("{ \"key\": \"new_value\" }")) // Request body
                        .check(status().is(200))); // Check for a 200 OK response
        return null;
    }

    private static <T> T payload(final JsonObject endpoint, final String payloadType) {
        if ("JsonString".equalsIgnoreCase(payloadType)) {
            return (T) StringBody(endpoint.get("payload").getAsString());
        }
        return null;
    }
}