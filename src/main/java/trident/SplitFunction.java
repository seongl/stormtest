package trident;

import org.apache.storm.trident.operation.TridentCollector;
import org.apache.storm.trident.tuple.TridentTuple;
import org.apache.storm.tuple.Values;
import org.mozilla.javascript.BaseFunction;

/**
 * Created by slee8 on 11/6/17.
 */
public class SplitFunction extends org.apache.storm.trident.operation.BaseFunction {

    @Override
    public void execute(TridentTuple tuple, TridentCollector collector) {
        String sentence = tuple.getString(0);
        String[] words = sentence.split(" ");
        for(String word : words) {
            word = word.trim();
            if(!word.isEmpty()) {
                collector.emit(new Values(word));
            }
        }


    }
}
