package wordcount;

import org.apache.storm.shade.org.jgrapht.experimental.permutation.IntegerPermutationIter;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by slee8 on 11/5/17.
 */
public class WordCounter extends BaseBasicBolt {

    Map<String, Integer> counters;
    Integer id;
    String name;
    String fileName;

    public void prepare(Map stormConf, TopologyContext context) {
        this.counters = new HashMap<String, Integer>();
        this.name = context.getThisComponentId();
        this.id = context.getThisTaskId();
        this.fileName = stormConf.get("dirToWrite").toString() +
                "output" + "-" + name + id + ".txt";
    }

    @Override
    public void execute(Tuple tuple, BasicOutputCollector basicOutputCollector) {
        String word = tuple.getString(0);

        if(!counters.containsKey(word)) {
            counters.put(word, 1);
        } else {
            Integer c = counters.get(word) + 1;
            counters.put(word, c);
        }

    }

    public void cleanup() {
        try {
            PrintWriter writer = new PrintWriter(fileName, "UTF-8");

            for(Map.Entry<String, Integer> entry : counters.entrySet()) {
                writer.println(entry.getKey() + ": " + entry.getValue());
            }
            writer.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }
}
