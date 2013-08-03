# genetic-akka

A framework for rapid deployment of genetic algorithm, built using Scala and Akka for maximum parallelism and configurability.

## Key features

* Simple interfaces to allow rapid design of algorithms.
* High level parallelism provided by Akka to maximise CPU usage and available threads.
* Akka configuration file allows control of threadpools for specific environments outside of code.
* Deploy to distributed systems with no changes to code.
* Asynchronous messages allow population snapshots to be taken at any moment.
* Persistence layer for chromosomes provided by Hibernate/JPA.

## Usage

Source code is intended to be run using [sbt](http://www.scala-sbt.org/).

Please refer to unit tests for example usage.

In order to run the unit tests, first execute ```sbt package``` to ensure the Akka configuration file is accessible.

## Configuration

Configure the Akka Actor system by editing src/main/resources/reference.conf

In addition to the usual Akka configuration steps, the following lines determine the number of workers Actors that will be spawned to evaluate the fitness functions of the population.

```
#number of workers (Hosts) to spawn
workers = XX
```

A key consideration when determining the amount of workers is the memory required to evaluate the fitness function.  Users will need to estimate the amount of memory required for their specific use and aim to ensure memory required * workers < maximum heap size.

## License

This software is licensed under the Apache 2 license, quoted below.

Copyright (c) 2013 Simon Todd <simon@sltodd.co.uk>

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
