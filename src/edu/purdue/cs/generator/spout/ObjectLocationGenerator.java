package edu.purdue.cs.generator.spout;

import java.util.Map;
import java.util.Random;

import edu.cs.purdue.edu.helpers.Constants;
import edu.cs.purdue.edu.helpers.RandomGenerator;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class ObjectLocationGenerator extends BaseRichSpout {

	private static final long serialVersionUID = 1L;
	private SpoutOutputCollector collector;
	private RandomGenerator randomGenerator;

	public void ack(Object msgId) {
		System.out.println("OK:" + msgId);
	}

	public void close() {
	}

	public void fail(Object msgId) {
		System.out.println("FAIL:" + msgId);
	}

	public void nextTuple() {
	//	while (true) {
			int id = randomGenerator.nextInt(Constants.numMovingObjects);
			Double xCoord = randomGenerator.nextDouble(0,Constants.xMaxRange);
			Double yCoord = randomGenerator.nextDouble(0,Constants.yMaxRange);
			this.collector.emit(new Values(id, xCoord, yCoord));
			try {
				if (Constants.dataGeneratorDelay != 0)
					Thread.sleep(Constants.dataGeneratorDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		//}
	}

	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {
		this.collector = collector;
		randomGenerator = new RandomGenerator(Constants.generatorSeed);
	}

	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields(Constants.objectIdField,
				Constants.objectXCoordField, Constants.objectYCoordField));
	}
}
