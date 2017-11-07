import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

import java.util.Map;
import java.util.Random;

/**
 * Created by slee8 on 11/7/17.
 */
public class RheosSpout extends BaseRichSpout {
    SpoutOutputCollector _collector;

    @Override
    public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector collector) {
        _collector = collector;

    }

    @Override
    public void nextTuple() {
        System.out.println("Yo ");
        _collector.emit(new Values("Yo "));
    }

    @Override
    public void ack(Object id) {
    }

    @Override
    public void fail(Object id) {
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word"));
    }

}
