package edu.purdue.cs.baseline.range;

import backtype.storm.Config;
import backtype.storm.StormSubmitter;
import backtype.storm.topology.TopologyBuilder;
import edu.cs.purdue.edu.helpers.Constants;
import edu.cs.purdue.edu.helpers.KillTopology;
import edu.purdue.cs.baseline.range.bolt.IncrementalRangeFilter;
import edu.purdue.cs.baseline.range.bolt.NonIncrementalRangeFilter;
import edu.purdue.cs.generator.spout.ObjectLocationGenerator;
import edu.purdue.cs.generator.spout.RangeQueryGenerator;
import edu.purdue.cs.performance.ClusterInformationExtractor;

public class IncrementalTopologySubmitter {





	public static void main(String[] args) throws Exception {
         
		TopologyBuilder builder = new TopologyBuilder();
	
		builder.setSpout(Constants.objectLocationGenerator, new ObjectLocationGenerator(),Constants.dataSpoutExecuters).setNumTasks(Constants.dataSpoutParallelism);
		builder.setSpout(Constants.queryGenerator, new RangeQueryGenerator(),Constants.querySpoutParallelism);
		builder.setBolt(Constants.rangeFilterBolt,
							  new IncrementalRangeFilter(),Constants.boltParallelism).allGrouping(Constants.queryGenerator).shuffleGrouping(Constants.objectLocationGenerator);
		
		String topologyName ="IncrementalRange-Queries_toplogy";
		System.out.println("******************************************************************************************************");
		System.out.println(topologyName);
		Config conf = new Config();
		conf.setDebug(false);
        //Topology run
		conf.setNumWorkers(Constants.numberOfWorkers);
		conf.put(Config.TOPOLOGY_ACKER_EXECUTORS,0);
		conf.put(Config.NIMBUS_HOST, Constants.mcMachinesNimbus);
		System.setProperty("storm.jar", "target/StormTestNaieve-0.0.1-SNAPSHOT.jar");
		//LocalCluster cluster = new LocalCluster();
		StormSubmitter.submitTopology(topologyName, conf, builder.createTopology());
		Thread.sleep(1000 * 60 * Constants.minutesToGetSats);
		ClusterInformationExtractor.main(null);
		KillTopology.killToplogy(topologyName, Constants.mcMachinesNimbus);
		System.out.println("******************************************************************************************************");
	}
}
