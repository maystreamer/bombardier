package maystreamer.simulation;

import com.google.gson.JsonObject;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.stressPeakUsers;

public class StressTestSimulation extends AbstractSimulation {
    final JsonObject stress = injectionProfile.getAsJsonObject("stress");

    final int total_test_duration = Integer.parseInt(stress.get("total_test_duration").getAsString());
    final int total_injected_users = Integer.parseInt(stress.get("total_injected_users").getAsString());

    {
        // Set up the load testing configuration
        setUp(scenarioBuilder.injectOpen(
                stressPeakUsers(total_injected_users).during(total_test_duration)
        )).assertions(
                        global().responseTime().percentile(percentile).lte(responseTimeLessThan.intValue()),
                        global().successfulRequests().percent().gt(successRatioPercentageHigherThan)
                )
                .protocols(httpProtocolBuilder);
    }
}
