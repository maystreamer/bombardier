# Bombardier

![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)

## Description

No-Code Performance Testing Powered by Gatling. You can run simulations for the Capacity, Smoke, and Soak Test. Configurations for specific simulations, acceptance criteria, and APIs to load test can be done [here](https://github.com/maystreamer/bombardier/blob/main/src/test/resources/configuration.json).

Before we delve deeper, let’s briefly explore the concept of performance testing.

**Performance testing** is crucial to assessing how well your system accommodates varying levels of throughput and traffic. While an application may function seamlessly with a few active users, performance issues often arise when user demand surges. The primary goal of performance testing is to identify and ultimately mitigate these potential issues.

Types of performance testing:
- **Capacity Testing**  :  Evaluating a system's performance under a predetermined volume of users and traffic.
- **Stress Testing**    :  Gradually increasing the load on a system to identify its breaking point.
- **Soak Testing**      :  Sustaining a consistent traffic rate over an extended period to pinpoint bottlenecks.

Through performance testing, you can expect to gather valuable metrics such as:
- **Transaction Response Times**  :  The duration it takes for the server to respond to a request.
- **Throughput**                  :  The number of transactions the system can handle over a specified timeframe.
- **Errors**                      :  The occurrence of error messages, such as timeouts, that may emerge during load testing.

The primary objective of this project is to develop a No-Code load-testing platform that enables users to conduct API load tests effortlessly. By adding APIs and adjusting load configurations, users can initiate tests without repetitive coding for each new API.

## Features

- Run Capacity Tests, Smoke Tests, and Soak Tests
- Easy configuration through JSON
- No coding required for new APIs
- Customizable load configurations

## Prerequisites

Make sure you have the following installed:

- [Java JDK](https://openjdk.org/projects/jdk/) (version 20 or greater)
- [Gradle](https://gradle.org/install/) (version latest)
- [Gradle Plugin](https://docs.gatling.io/reference/integrations/build-tools/gradle-plugin/)
  
## Getting Started

Follow these steps to get a copy of the project up and running on your local machine for development and testing purposes.  To update the load configurations and add your own APIs, you may make changes in the configuration file [here](https://github.com/maystreamer/bombardier/blob/main/src/test/resources/configuration.json). For testing purposes, it is recommended to use the default load configurations.

```
git clone https://github.com/maystreamer/bombardier.git
```

```
cd bombardier
```

```
./mvnw clean build
```

```
./mvnw gatling:test -Dgatling.simulationClass=maystreamer.simulation.SoakTestSimulation -Dscenario.toRun="User_Life_Cycle"
```

or, to choose a simulation

```
./mvnw gatlingRun
```

You will be prompted to **Choose a simulation number**:
- [0] maystreamer.simulation.CapacityTestSimulation
- [1] maystreamer.simulation.SoakTestSimulation
- [2] maystreamer.simulation.StressTestSimulation

choose the simulation number and press Enter

## Sample report
On your local machine, you can access your load run report from ***build/reports/gatling***

A sample report to view on your local browser can be downloaded from [here](https://github.com/maystreamer/bombardier/tree/main/reports/gatling/capacitytestsimulation-20241002205016620)
or
you can view a sample pdf report [here](https://github.com/maystreamer/bombardier/blob/main/capacity_simulation_sample_report.pdf)
