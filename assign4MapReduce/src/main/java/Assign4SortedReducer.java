import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

public class Assign4SortedReducer extends Reducer<IntWritable, Text, Text, IntWritable> {
    protected void reduce(IntWritable key, Iterable<Text> values,Context context) throws InterruptedException, IOException, IOException {
        for(Text value : values){
            context.write(value, key);
        }
    }
}