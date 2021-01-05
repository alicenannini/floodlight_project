Source code from: https://github.com/floodlight/floodlight (v1.1)

# A Dynamic Multipath Scheduling Protocol (DMSP) in SDN 
## How it works
In the Floodlight VM, after downloading our project from this repository: 
- Open eclipse and run the floodlight project \
or
- From terminal:
```
    cd floodlight
    ant
    java -jar target/floodlight.jar
```
Then from another terminal, start mininet (insert your own controller ip):
```
    cd floodlight
    sudo mn --custom fat-tree.py --controller=remote,ip=<controller-ip>,port=6653 –topo=fat-tree
```
Now you can start pinging between the hosts!
