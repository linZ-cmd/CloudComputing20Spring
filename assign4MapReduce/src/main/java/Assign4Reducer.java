import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

public class Assign4Reducer extends Reducer<Text, IntWritable, Text, IntWritable> {
    public void reduce(Text	key, Iterable<IntWritable> values, Context context) throws IOException,InterruptedException {
        Iterator<IntWritable> iterator = values.iterator();

        // count the frequency of each word
        int count = 0;

        while (iterator.hasNext()) {
            IntWritable value = iterator.next();
            count += value.get();
        }

        context.write(key, new IntWritable(count));
    }
}