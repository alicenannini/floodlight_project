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
- Added class [PathId](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/topology/TopologyManager.java#L97-L155), used for the sheduling of the multipaths
- Modified [getRoute()](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/topology/TopologyManager.java#L779-L802) to schedule the paths with Round Robin
- Added [scheduleNewRoute()](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/topology/TopologyManager.java#L804-L821) that performs the Round Robin scheduling
- Added [checkStatistics()](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/topology/TopologyManager.java#L370-L379) that's called by the [UpdateTopologyWorker](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/topology/TopologyManager.java#L350) and recompute the paths each time new statistics are found

[TopologyInstance.java](src/main/java/net/floodlightcontroller/topology/TopologyInstance.java):
- Modified the [pathcache](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L93-L108) so that for each RouteId we can save more than one possible path
- Modified the class [NodeDist](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L453) so that the [equals()](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L480)funcion consider also the cost, and not only the node id
- Modified [dijkstra()](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L496-L561) considering all next-hops with equal costs. The costs can be computed also based on the traffic load on each link [code line](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L534). Moreover, there is the possibility of discard congested links in the computation of the next-hops [code lines](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L541-L542).
- Added [getTrafficLoad()](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L563-L571) to compute the load on a link, and [isLinkCongested()](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L573-L592)  to check if a given link has too much traffic load on it: it compares the tx bits per second to a threshold, defined in [IStatisticsService.java](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/statistics/IStatisticsService.java#L14).
- Modified the [buildroute()](https://github.com/alicenannini/floodlight_project/blob/646ed611e4812fb996a74f94dd082ea360d72bc9/src/main/java/net/floodlightcontroller/topology/TopologyInstance.java#L655-L727) so that it consider all the possible next-hops, builds all the possible paths, and saves them in the "pathcache"


### package [net.floodlightcontroller.statistics](src/main/java/net/floodlightcontroller/statistics)
We added several classes:
- [IStatisticsService.java](src/main/java/net/floodlightcontroller/statistics/IStatisticsService.java): interface of the service
- [StatisticsCollector.java](src/main/java/net/floodlightcontroller/statistics/StatisticsCollector.java): implements the interface and contains the threads that collect the bandwidth statistics
- [SwitchPortBandwidth.java](src/main/java/net/floodlightcontroller/statistics/SwitchPortBandwidth.java): utility class to store statistics for each port
- The other classes in the package are for the Rest API service

### package [net.floodlightcontroller.routing](src/main/java/net/floodlightcontroller/routing)
[BroadcastTree.java](src/main/java/net/floodlightcontroller/routing/BroadcastTree.java):
- Instead of saving one next-hop for each destination, we save a list of all minimum cost next-hops [code](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/routing/BroadcastTree.java#L29) and we added some utility functions to work with the list of next-hops [code](https://github.com/alicenannini/floodlight_project/blob/a25e7748311e458000218f94ec70b845cdede12b/src/main/java/net/floodlightcontroller/routing/BroadcastTree.java#L83-L102)


