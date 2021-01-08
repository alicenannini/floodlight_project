from mininet.topo import Topo

class FatTree( Topo ):
    
    def addSwitch( self, name, **opts ):
        kwargs = { 'protocols' : 'OpenFlow13' }
        kwargs.update( opts )
        return super(FatTree, self).addSwitch( name, **kwargs )
    
    def __init__(self):
		'''Create the topology'''
		K = 4

		# Initialize Topology
		Topo.__init__( self )

		cores = []
		aggregates = []
		edges = []

		# Create the 4 core switches
		for i in range(K):
			sn = i+1
			cores.append(self.addSwitch( 's%d' %sn ) )
		

		# Now create the aggregate switches, and connect
		aK = 8 

		for i in range(aK):
			sn = i+1
			aggregates.append(self.addSwitch('s1%d' %sn))
			edges.append(self.addSwitch('s11%d' %sn))
			h1 = self.addHost( 'h%d1' % sn)
			self.addLink( h1, edges[i] )
			h2 = self.addHost( 'h%d2' % sn )
			self.addLink( h2, edges[i] )
			if( (i%2) == 0 ):
				self.addLink( aggregates[i], cores[0] )
				self.addLink( aggregates[i], cores[1] )
			else:
				self.addLink( aggregates[i], cores[2] )
				self.addLink( aggregates[i], cores[3] )
				# Add links between aggrs and edges
				self.addLink( aggregates[i], edges[i] )
				self.addLink( aggregates[i], edges[i-1] )
				self.addLink( aggregates[i-1], edges[i] )
				self.addLink( aggregates[i-1], edges[i-1] )

topos = { 'fat-tree': ( lambda: FatTree() ) }

'''   
sudo mn --custom fat-tree.py  --controller=remote,ip=192.168.56.110,port=6653 --topo=fat-tree
'''
