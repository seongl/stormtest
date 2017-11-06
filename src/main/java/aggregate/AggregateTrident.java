package aggregate;

import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.trident.TridentTopology;
import org.apache.storm.tuple.Fields;
import trident.SplitFunction;
import wordcount.WordReader;

/**
 * Created by slee8 on 11/6/17.
 */
public class AggregateTrident {
    public static void main(String[] args) throws InvalidTopologyException, AuthorizationException, AlreadyAliveException {
        TridentTopology topology = new TridentTopology();
        topology.newStream("lines", new WordReader())
                .each(new Fields("word"),
                        new SplitFunction(),
                        new Fields("word_split"));

        Config conf = new Config();
        conf.setDebug(true);
        conf.put("fileToRead", "/Users/slee8/sample.txt");

        if(args.length != 0 && args[0].equals("remote")) {
            StormSubmitter.submitTopology("Trident-Topology", conf, topology.build());
        } else {

        }

    }
}
