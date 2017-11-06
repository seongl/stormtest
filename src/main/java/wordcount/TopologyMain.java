package wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;

/**
 * Created by slee8 on 11/6/17.
 */
public class TopologyMain {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-counter", new WordCounter(), 2)
                .allGrouping("word-reader");
//                .fieldsGrouping("word-reader", new Fields("word"));
//                .shuffleGrouping("word-reader");

        Config conf = new Config();
        conf.put("fileToRead", "/Users/slee8/sample.txt");
        conf.put("dirToWrite", "/Users/slee8/wordCountoutput/");
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        try {
            cluster.submitTopology("WordCoutner-Topology", conf, builder.createTopology());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cluster.shutdown();
        }
    }
}
