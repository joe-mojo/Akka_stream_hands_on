# Akka_Stream_hands_on
Akka stream exercize.

Excerpt from a challenge, the goal is to find the string that gives the MD5 value `dae1d529b16ad4af420f4fd54840a0e4`; 
hint: the value looks like a 8 length number, wrapped by square brackets. Example : `[12345678]`

Here, the logic is given. You have to create the flows with Akka stream.

[All Akka docs here](https://akka.io/docs/)

## Agenda
1. [Simple source/map/sink flow](#simple-flow)
2. [Graph with source/flow/sink](#simple-graph-using-graphdsl)
3. [Graph with parallelized flows](#graph-with-parallelized-flows-using-graphdsl)

## Simple flow

Create a source of numbers, map it to a (number, hash) tuple, and add a sink to display the first matching hash.

![Simple flow](doc/Simple%20flow.png)

## Simple graph using GraphDSL

 - Create a source of numbers, connect it to a flow that takes numbers as input and gives a tuple (number, hash) as output.
The flow must stop on first matching hash. 
 - Connect a sink the output of the flow to see progress (optional) and result.

![Simple flow](doc/simple%20flow%20graph.png)

## Graph with parallelized flows using GraphDSL

 - Create a source of numbers.
 - Create a balancer that takes numbers as input and balance them into as much output as requested by a command line arg.
 - Create a merge that takes as much input (number, hash) as requested, and outputs (number, hash). It must stop on furst matching hash.
 - Create a flow that takes numbers as input and gives a tuple (number, hash) as output.
 - Connect balance to parallel flows and flows to merge. Connect Source to balance and merge to sink. 

![Parralel flows](doc/parrallel%20flows.png)

## Bonus : Graph with parallelized flows and kill switches
 - Create a source of numbers.
 - Create a balancer that takes numbers as input and balance them into as much output as requested by a command line arg.
 - Create a flow that takes numbers as input and gives a tuple (number, hash) as output.
 - Connect balance to parallel flows and each flow to a sink
 - Insert a kill switch before each sink. The first flow to win push the red button. BOOM !

