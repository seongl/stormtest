//import org.apache.storm.topology.BasicOutputCollector;
//import org.apache.storm.topology.OutputFieldsDeclarer;
//import org.apache.storm.topology.base.BaseBasicBolt;
//import org.apache.storm.tuple.Tuple;


import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
//import org.apache.commons.lang3.StringUtils;

import java.util.Map;

public class PrinterBolt extends BaseRichBolt {

    private static final long serialVersionUID = 1L;

    private OutputCollector collector;
//    private transient AvroDeserializer avroDeserializer;
//    private StreamInputProcessor streamInputProcessor;
//    private BeaconConfigCache beaconConfigCache;
//    protected static volatile Boolean isCacheRefreshed = false;
//    private String taskId ;
//    private UserInputParams userInputParams;

    @Override
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        this.collector = outputCollector;
        System.out.println("here");
    }

    @Override
    public void execute(Tuple tuple) {
        System.out.println("AAA");
        System.out.println(tuple);
        collector.ack(tuple);
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        System.out.println("here2");
//        outputFieldsDeclarer.declare(new Fields("nativeAppFilterBoltId"));

    }
}
