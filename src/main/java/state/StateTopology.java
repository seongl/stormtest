package state;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.trident.TridentState;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.trident.operation.builtin.MapGet;
import org.apache.storm.trident.testing.MemoryMapState;
import org.apache.storm.tuple.Fields;
import trident.SplitFunction;
import wordcount.WordReader;

/**
 * Created by slee8 on 11/6/17.
 */
public class StateTopology {
    public static void main(String[] args) {
        TridentTopology topology = new TridentTopology();

        TridentState wordCounts =
                topology.newStream("lines", new WordReader())
                .each(new Fields("word"),
                        new SplitFunction(),
                        new Fields("word_split"))
                .groupBy(new Fields("word_split"))
                .persistentAggregate(new MemoryMapState.Factory(), new Count(), new Fields("count"));

        LocalDRPC drpc = new LocalDRPC();

        topology.newDRPCStream("count", drpc)
                .stateQuery(wordCounts, new Fields("args"), new MapGet(), new Fields("count"));

        // Configuration
        Config conf = new Config();
        conf.setDebug(true);
        conf.put("fileToREad", "/Users/slee8/sample.txt");

        LocalCluster cluster = new LocalCluster();

        try {
            cluster.submitTopology("trident-topology", conf, topology.build());
            Thread.sleep(20000);
            for(String word : new String[]{"short", "very"}) {
                System.out.println("Result for " + word +  ": " + drpc.execute("count", word));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }
}
