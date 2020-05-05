import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

public class Assign4SortedMapper extends Mapper<Object, Text, IntWritable, Text> {

    protected void map(Object key, Text value, Context context) throws IOException, InterruptedException, IOException {
        String[] splits = value.toString().split("\t");
        context.write(new IntWritable(Integer.parseInt(splits[1])), new Text(splits[0]));
    }
}