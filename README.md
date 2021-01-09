Source code from: https://github.com/floodlight/floodlight (v1.1)

# A Dynamic Multipath Scheduling Protocol (DMSP) in SDN 
## How it works
In the Floodlight VM, after downloading our project from this repository: 
-  From terminal type the following commands to setup for importing the project in Eclipse:
```
    mv classpath.txt .classpath
    mv project.txt .project
```
- Open eclipse, import and run the floodlight project 

or 

- From terminal:
```
    cd floodlight_project
    ant
    java -jar target/floodlight.jar
```
\
Then from another terminal, start mininet (insert your own controller ip) using the topology defined in [fat-tree.py](fat-tree.py):
```
    cd floodlight_project
    sudo mn --custom fat-tree.py --controller=remote,ip=<controller-ip>,port=6653 –topo=fat-tree
```
Now you can start pinging between the hosts!

## Test commands
From the mininet console:
- to check the overall connectivity:
```
    pingall
```
- to ping between two hosts:
```
    h11 ping -c 1 h41
```
- to congestion links in the network:
```
    xterm h41
        iperf -s -u
       
    xterm h11
        iperf -c 10.0.0.7 -u -b 100M
```
- to capture the packets and check the paths:
```
    xterm s1
        wireshark
```

## Main files that we work with
### package [net.floodlightcontroller.topology](src/main/java/net/floodlightcontroller/topology)
[TopologyManager.java](src/main/java/net/floodlightcontroller/topology/TopologyManager.java):
- Added "class PathId", used for the sheduling of the multipaths
- Modified "getRoute()" at lines [780-802] to schedule the paths with Round Robin
- Added "scheduleNewRoute()" at lines [805-821] that perform the Round Robin scheduling
- Added "checkStatistics()" that's called by the UpdateTopologyWorker and recompute the paths each time new statistics are found

[TopologyInstance.java](src/main/java/net/floodlightcontroller/topology/TopologyInstance.java):
- Modified the "pathcache" so that for each RouteId we can save more than one possible path
- Modified the class "NodeDist" so that the "equals()" funcion consider also the cost, and not only the node id
- Modified "dijkstra()" considering all next-hops with equal costs. The costs can be computed also based on the traffic load on each link [line 534]. Moreover, there is the possibility of discard congested links in the computation of the next-hops [lines 541-542].
- Added getTrafficLoad() to compute the load on a link [line 563], and isLinkCongested()  to check if a given link has too much traffic load on it [line 573]: it compares the tx bits per second to a threshold, defined in the IStatisticsService.java class.
- Modified the "buildroute()" so that it consider all the possible next-hops, builds all the possible paths, and saves them in the "pathcache"


### package [net.floodlightcontroller.statistics](src/main/java/net/floodlightcontroller/statistics)
We added several classes:
- [IStatisticsService.java](src/main/java/net/floodlightcontroller/statistics/IStatisticsService.java): interface of the service
- [StatisticsCollector.java](src/main/java/net/floodlightcontroller/statistics/StatisticsCollector.java): implements the interface and contains the threads that collect the bandwidth statistics
- [SwitchPortBandwidth.java](src/main/java/net/floodlightcontroller/statistics/SwitchPortBandwidth.java): utility class to store statistics for each port
- The other classes in the package are for the Rest API service

### package [net.floodlightcontroller.routing](src/main/java/net/floodlightcontroller/routing)
[BroadcastTree.java](src/main/java/net/floodlightcontroller/routing/BroadcastTree.java):
- Instead of saving one next-hop for each destination, we save a list of all minimum cost next-hops


