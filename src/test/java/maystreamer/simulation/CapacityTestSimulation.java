package maystreamer.simulation;

import com.google.gson.JsonObject;

import static io.gatling.javaapi.core.CoreDsl.global;
import static io.gatling.javaapi.core.CoreDsl.rampUsersPerSec;

public class CapacityTestSimulation extends AbstractSimulation {

    public CapacityTestSimulation() {
        final JsonObject capacity = injectionProfile.getAsJsonObject("capacity");

        final int initial_user_arrival_rate = Integer.parseInt(capacity.get("initial_user_arrival_rate").getAsString());
        final int final_user_arrival_rate = Integer.parseInt(capacity.get("final_user_arrival_rate").getAsString());
        final int total_test_duration = Integer.parseInt(capacity.get("total_test_duration").getAsString());

        {
            // Set up the load testing configuration
            setUp(scenarioBuilder.injectOpen(
                    rampUsersPerSec(initial_user_arrival_rate).to(final_user_arrival_rate).during(total_test_duration
                    ))).assertions(
                            global().responseTime().percentile(percentile).lte(responseTimeLessThan.intValue()),
                            global().successfulRequests().percent().gt(successRatioPercentageHigherThan)
                    )
                    .protocols(httpProtocolBuilder);
        }
    }
}
