package maystreamer;

import maystreamer.simulation.AbstractSimulation;
import maystreamer.simulation.CapacityTestSimulation;
import maystreamer.simulation.SoakTestSimulation;
import maystreamer.simulation.StressTestSimulation;

import java.util.Arrays;

public class Runner {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("No simulation specified. Running CapacityTestSimulation by default.");
            runSimulation(new CapacityTestSimulation());
        } else {
            Arrays.stream(args).forEach(simulationName -> {
                switch (simulationName) {
                    case "CapacityTestSimulation":
                        runSimulation(new CapacityTestSimulation());
                        break;
                    case "StressTestSimulation":
                        runSimulation(new StressTestSimulation());
                        break;
                    case "SoakTestSimulation":
                        runSimulation(new SoakTestSimulation());
                        break;
                    default:
                        System.err.println("Unknown simulation: " + simulationName);
                }
            });
        }
    }

    private static void runSimulation(AbstractSimulation simulation) {
        // Run the simulation using Gatling

    }
}
