package net.floodlightcontroller.statistics;

import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.topology.NodePortTuple;

public interface IStatisticsService extends IFloodlightService {
		
	String setPortStatsPeriod(int period);

	SwitchPortBandwidth getBandwidthConsumption(DatapathId dpid, OFPort p);
		
	Map<NodePortTuple, SwitchPortBandwidth> getBandwidthConsumption();
	
	void collectStatistics(boolean collect);

	boolean isStatisticsCollectionEnabled();
}