/**
*    Copyright 2011, Big Switch Networks, Inc. 
*    Originally created by David Erickson, Stanford University
* 
*    Licensed under the Apache License, Version 2.0 (the "License"); you may
*    not use this file except in compliance with the License. You may obtain
*    a copy of the License at
*
*         http://www.apache.org/licenses/LICENSE-2.0
*
*    Unless required by applicable law or agreed to in writing, software
*    distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
*    WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
*    License for the specific language governing permissions and limitations
*    under the License.
**/

package net.floodlightcontroller.routing;
import java.util.HashMap;
import java.util.List;

import net.floodlightcontroller.routing.Link;

import org.projectfloodlight.openflow.types.DatapathId;

public class BroadcastTree {
    protected HashMap<DatapathId, List<Link>> links;
    protected HashMap<DatapathId, Integer> costs;
    protected HashMap<DatapathId, Integer> lastScheduledLinks;

    public BroadcastTree() {
        links = new HashMap<DatapathId, List<Link>>();
        costs = new HashMap<DatapathId, Integer>();
        lastScheduledLinks = new HashMap<DatapathId, Integer>();
    }

    public BroadcastTree(HashMap<DatapathId, List<Link>> links, HashMap<DatapathId, Integer> costs) {
        this.links = links;
        this.costs = costs;
        this.lastScheduledLinks = new HashMap<DatapathId, Integer>();
        for (DatapathId node : costs.keySet())
        	this.lastScheduledLinks.put(node, 0);
        
    }
    
    public BroadcastTree(HashMap<DatapathId, List<Link>> links, HashMap<DatapathId, Integer> costs, HashMap<DatapathId, Integer> l) {
        this.links = links;
        this.costs = costs;
        this.lastScheduledLinks = l;
    }

    public List<Link> getTreeLink(DatapathId node) {
        return links.get(node);
    }

    public int getCost(DatapathId node) {
        if (costs.get(node) == null) return -1;
        return (costs.get(node));
    }

    public HashMap<DatapathId, List<Link>> getLinks() {
        return links;
    }

    public void addTreeLink(DatapathId myNode, List<Link> link) {
        links.put(myNode, link);
        if(this.lastScheduledLinks.get(myNode) == null)
        	this.lastScheduledLinks.put(myNode, 0);
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for(DatapathId n: links.keySet()) {
            sb.append("[" + n.toString() + ": cost=" + costs.get(n) + ", " + links.get(n) + "]");
        }
        return sb.toString();
    }

    public HashMap<DatapathId, Integer> getCosts() {
        return costs;
    }
    
    public HashMap<DatapathId, Integer> getLastScheduledLinks() {
    	return lastScheduledLinks;
    }
    
    public void setLastScheduledLink(HashMap<DatapathId, Integer> l) {
    	this.lastScheduledLinks = l;
    }
    
    public Link getScheduledLink(DatapathId node){
    	int scheduledLink = lastScheduledLinks.get(node);
    	//System.err.println("scheduleLink: "+scheduledLink);
    	return links.get(node).get(scheduledLink);
    }
    
    public Link scheduleLink(DatapathId node) {
    	int len = links.get(node).size();
    	int scheduledLink = (lastScheduledLinks.get(node)+1)%len;
    	lastScheduledLinks.put(node, scheduledLink);
    	return links.get(node).get(scheduledLink);
    }
}
