import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.io.PrintWriter;
import java.util.Map;

/**
 * Created by slee8 on 11/3/17.
 */
public class YfBolt extends BaseBasicBolt {

    @Override
    public void prepare(Map stormConf, TopologyContext context) {}

    @Override
    public final void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String symbol = tuple.getValue(0).toString();
        String timestamp = tuple.getString(1);

        Double price = (Double)tuple.getValueByField("price");
        Double prevClose = tuple.getDoubleByField("prev_close");

        Boolean gain = true;

        if(price <= prevClose) {
            gain = false;
        }

        basicOutputCollector.emit(new Values(symbol, timestamp, price, gain));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("company", "timestamp", "price", "gain"));
    }

    @Override
    public void cleanup() {}
}
