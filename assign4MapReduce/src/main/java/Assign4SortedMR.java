import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

public class Assign4SortedMR {
    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration();
        String[] otherargs = new GenericOptionsParser(conf, args).getRemainingArgs();
        if(otherargs.length != 2) {
            System.out.println("please enter 2 arguments: input path and output path");
            System.exit(2);
        }

        Job job = Job.getInstance(conf, "word_cunt");
        job.setJarByClass(Assign4SortedMR.class);
        job.setMapperClass(Assign4SortedMapper.class);
        job.setReducerClass(Assign4SortedReducer.class);

        job.setMapOutputValueClass(Text.class);
        job.setMapOutputKeyClass(IntWritable.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setSortComparatorClass(DecreasingComparator.class);

        FileInputFormat.setInputPaths(job, new Path(otherargs[0]));
        FileOutputFormat.setOutputPath(job, new Path(otherargs[1]));
        System.exit(job.waitForCompletion(true)?0:1);
    }
}