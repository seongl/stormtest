package drpc;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.LocalDRPC;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.trident.operation.builtin.Count;
import org.apache.storm.tuple.Fields;
import trident.SplitFunction;
import wordcount.WordReader;

/**
 * Created by slee8 on 11/6/17.
 */
public class DrpcTopology {
    public static void main(String[] args) {
        LocalDRPC drpc = new LocalDRPC();

        TridentTopology topology = new TridentTopology();
//        topology.newStream("lines", new WordReader())
        topology.newDRPCStream("split", drpc)
                .each(new Fields("args"),
                        new SplitFunction(),
                        new Fields("word_split"))
                .groupBy(new Fields("word_split"))
                .aggregate(new Count(), new Fields("count"));

        Config conf = new Config();
        conf.setDebug(true);

        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("trident-topology", conf, topology.build());
        for(String word : new String[]{ "a very avery short book", "this is a very very long book"}) {
            System.out.println("Result for " + word + ": " + drpc.execute("split", word));
        }

        cluster.shutdown();
    }
}
