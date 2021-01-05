package net.floodlightcontroller.statistics;

import java.util.Map;

import org.projectfloodlight.openflow.types.DatapathId;
import org.projectfloodlight.openflow.types.OFPort;
import org.projectfloodlight.openflow.types.U64;

import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.topology.NodePortTuple;

public interface IStatisticsService extends IFloodlightService {
	
	static final U64 TX_THRESHOLD = U64.of(60000); // 90 Mbps
	
	U64 getTxThreshold();
	
	String setPortStatsPeriod(int period);

	SwitchPortBandwidth getBandwidthConsumption(DatapathId dpid, OFPort p);
		
	Map<NodePortTuple, SwitchPortBandwidth> getBandwidthConsumption();
	
	void collectStatistics(boolean collect);

	boolean isStatisticsCollectionEnabled();
		
	boolean isPortCongested(DatapathId dpid, OFPort p);
	
	boolean isNetworkCongested();
	
	void resetCongestion();
	
}