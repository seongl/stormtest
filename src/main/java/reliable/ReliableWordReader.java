package reliable;

import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Values;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by slee8 on 11/6/17.
 */
public class ReliableWordReader extends BaseRichSpout {

    private final static int MAX_FAILS = 10;
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private BufferedReader reader;

    private HashMap<Integer, String> allMessages;
    private ArrayList<Integer> toSend;
    private HashMap<Integer, Integer> msgFailureCount;

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;

        try {
            this.fileReader = new FileReader(conf.get("fileToRead").toString());
            this.reader = new BufferedReader(fileReader);
            this.allMessages = new HashMap<Integer, String>();
            this.toSend = new ArrayList<Integer>();

            int i=0;
            while(reader.readLine() != null) {
                allMessages.put(i++, reader.readLine());
                toSend.add(i);
            }

            this.msgFailureCount = new HashMap<Integer, Integer>();
        } catch (Exception e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
        }


    }

    @Override
    public void nextTuple() {
        if(!toSend.isEmpty()) {
            for(int msgId : toSend) {
                String word = allMessages.get(msgId);
                collector.emit(new Values(word), msgId);
            }
            toSend.clear();
        }



    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    @Override
    public void ack(Object msgId) {
        System.out.println("Sending message [" + msgId + "] successful!");
    }

    @Override
    public void fail(Object msgId) {
        Integer failedId = (Integer) msgId;

        Integer failures = 1;

        if(msgFailureCount.containsKey(failedId)) {
            failures = msgFailureCount.get(failedId) + 1;
        }

        if(failures < MAX_FAILS) {
            msgFailureCount.put(failedId, failures);

            toSend.add(failedId);

            System.out.println("Re-sending message [" + failedId + "]");
        } else {
            System.out.println("Sending message [" + failedId + "] failed!");
        }
    }
}
