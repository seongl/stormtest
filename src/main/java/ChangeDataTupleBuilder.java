
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.storm.kafka.spout.KafkaSpoutTupleBuilder;
import backtype.storm.tuple.Values;

import java.util.List;

public class ChangeDataTupleBuilder<K, V> extends KafkaSpoutTupleBuilder<K,V> {
    /**
     * @param topics list of topics that use this implementation to build tuples
     */
    public ChangeDataTupleBuilder(String... topics) {
        super(topics);
    }

    @Override
    public List<Object> buildTuple(ConsumerRecord<K, V> consumerRecord) {
        return new Values(consumerRecord.topic(),
                consumerRecord.partition(),
                consumerRecord.offset(),
                consumerRecord.key(),
                consumerRecord.value());
    }
}