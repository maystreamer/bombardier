package maystreamer.simulation;

import com.google.gson.JsonObject;

import static io.gatling.javaapi.core.CoreDsl.constantUsersPerSec;
import static io.gatling.javaapi.core.CoreDsl.global;

public class SoakTestSimulation extends AbstractSimulation {
    final JsonObject soak = injectionProfile.getAsJsonObject("soak");

    final int total_test_duration = Integer.parseInt(soak.get("total_test_duration").getAsString());
    final int constant_user_arrival_rate = Integer.parseInt(soak.get("constant_user_arrival_rate").getAsString());

    {
        // Set up the load testing configuration
        setUp(scenarioBuilder.injectOpen(
                constantUsersPerSec(constant_user_arrival_rate).during(total_test_duration)
        )).assertions(
                        global().responseTime().percentile(percentile).lte(responseTimeLessThan.intValue()),
                        global().successfulRequests().percent().gt(successRatioPercentageHigherThan)
                )
                .protocols(httpProtocolBuilder);
    }
}
