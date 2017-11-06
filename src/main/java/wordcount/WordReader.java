package wordcount;

import java.io.BufferedReader;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

/**
 * Created by slee8 on 11/5/17.
 */
public class WordReader extends BaseRichSpout {
    private SpoutOutputCollector collector;
    private FileReader fileReader;
    private BufferedReader reader;
    private boolean completed;

    @Override
    public void open(Map conf, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        this.collector = spoutOutputCollector;

        try {
            this.fileReader = new FileReader(conf.get("fileToRead").toString());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Error reading file [" + conf.get("wordFile") + "]");
        }
        this.reader = new BufferedReader(this.fileReader);
    }

    @Override
    public void nextTuple() {
        if(!completed) {
            try {
                String word = reader.readLine();

                if(word != null) {
                    word = word.trim();
                    word = word.toLowerCase();
                    collector.emit(new Values(word));
                } else {
                    completed = true;
                    fileReader.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Error reading tuple", e);
            }
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("word"));

    }
}
