package net.floodlightcontroller.mactracker;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.projectfloodlight.openflow.protocol.OFMessage;
import org.projectfloodlight.openflow.protocol.OFType;
import org.projectfloodlight.openflow.types.IpProtocol;
import org.projectfloodlight.openflow.util.HexString;

import net.floodlightcontroller.core.FloodlightContext;
import net.floodlightcontroller.core.IFloodlightProviderService;
import net.floodlightcontroller.core.IOFMessageListener;
import net.floodlightcontroller.core.IOFSwitch;
import net.floodlightcontroller.core.module.FloodlightModuleContext;
import net.floodlightcontroller.core.module.FloodlightModuleException;
import net.floodlightcontroller.core.module.IFloodlightModule;
import net.floodlightcontroller.core.module.IFloodlightService;
import net.floodlightcontroller.packet.Ethernet;
import net.floodlightcontroller.packet.ICMP;
import net.floodlightcontroller.packet.IPv4;

public class MACTracker implements IOFMessageListener, IFloodlightModule {

	protected IFloodlightProviderService floodlightProvider; // Reference to the provider
	
	@Override
	public String getName() {
		
		return MACTracker.class.getSimpleName();
	}

	@Override
	public boolean isCallbackOrderingPrereq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCallbackOrderingPostreq(OFType type, String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleServices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Class<? extends IFloodlightService>, IFloodlightService> getServiceImpls() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Class<? extends IFloodlightService>> getModuleDependencies() {
		
		Collection<Class<? extends IFloodlightService>> l = new ArrayList<Class<? extends IFloodlightService>>();
	    l.add(IFloodlightProviderService.class);
	    return l;
	}

	@Override
	public void init(FloodlightModuleContext context)
			throws FloodlightModuleException {
		
		floodlightProvider = context.getServiceImpl(IFloodlightProviderService.class);

	}

	@Override
	public void startUp(FloodlightModuleContext context)
			throws FloodlightModuleException {
		
		floodlightProvider.addOFMessageListener(OFType.PACKET_IN, this);

	}

	@Override
	public net.floodlightcontroller.core.IListener.Command receive(
			IOFSwitch sw, OFMessage msg, FloodlightContext cntx) {
		
		Ethernet eth = IFloodlightProviderService.bcStore.get(cntx, IFloodlightProviderService.CONTEXT_PI_PAYLOAD);
		
		if (! (eth.getPayload() instanceof IPv4))
			return Command.CONTINUE;
		
		IPv4 pkt = (IPv4) eth.getPayload();
		if ( pkt.getProtocol() == IpProtocol.ICMP ){
			ICMP icmp = (ICMP) pkt.getPayload();
			//if (icmp.getIcmpType() == ICMP.ECHO_REPLY || icmp.getIcmpType() == ICMP.ECHO_REQUEST){
				// Print the source MAC address
			    Long sourceMACHash = Ethernet.toLong(eth.getSourceMACAddress().getBytes());
			    //System.err.printf("MAC Address: {%s} seen on switch: {%s}\n",HexString.toHexString(sourceMACHash),sw.getId());   
			    
			//}
		}
		
	    return Command.CONTINUE;
	}

}
