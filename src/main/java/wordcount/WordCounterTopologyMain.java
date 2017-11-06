package wordcount;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.topology.TopologyBuilder;

/**
 * Created by slee8 on 11/5/17.
 */
public class WordCounterTopologyMain {
    public static void main(String[] args) {
        TopologyBuilder builder = new TopologyBuilder();
        builder.setSpout("word-reader", new WordReader());
        builder.setBolt("word-counter", new WordCounter(), 2).shuffleGrouping("word-reader");

        Config conf = new Config();
        conf.put("fileToRead", "/Users/slee8/Download/sample.txt");
        conf.put("dirToWrite", "/Users/slee8/Download/wordCountoutput/");
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        try {
            cluster.submitTopology("WordCounter-Topology", conf, builder.createTopology());
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            cluster.shutdown();
        }
    }
}
