import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

public class Assign4Mapper extends Mapper<Object, Text, Text, IntWritable> {
    // every time we find one word, we add 1 to its frequency
    IntWritable one = new IntWritable(1);
    // record each line of the input
    Text line = new Text();

    public void map(Object key, Text value, Context context) throws IOException,InterruptedException {
        StringTokenizer itr = new StringTokenizer(value.toString());
        while(itr.hasMoreTokens()) {
            line.set(itr.nextToken());
            // only read words not containing special character
            String[] words = line.toString().split("[^a-zA-Z]");
            for (String word : words) {
                context.write(new Text(word), one);
            }
        }
    }
}