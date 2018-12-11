/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */

import org.apache.hadoop.conf.*;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class mrtgenDriver extends Configured implements Tool {
			@Override
			public int run(String[] args) throws Exception {
				
				if (args.length != 3) {
					System.err.printf("Usage: [HASH] [/dir/input] [/dir/output]\n",
					getClass().getSimpleName());
					ToolRunner.printGenericCommandUsage(System.err);
					return -1;
				} else {
					
					Configuration cfg = new Configuration();
					cfg=getConf();
					cfg.set("hashedVal", args[0]);
					Job job = new Job(cfg, "Hashing");
					Job jobRev = new Job(cfg, "Hasing 2");

					jobRev.setJarByClass(mrtgenDriver.class);

					jobRev.setOutputKeyClass(Text.class);
					jobRev.setOutputValueClass(Text.class);

					jobRev.setMapperClass(MapRev.class);
					jobRev.setReducerClass(ReduceRev.class);

					jobRev.setInputFormatClass(TextInputFormat.class);
					jobRev.setOutputFormatClass(TextOutputFormat.class);
					jobRev.setNumReduceTasks(3);

					FileInputFormat.addInputPath(jobRev, new Path(args[1]));
					FileOutputFormat.setOutputPath(jobRev, new Path(args[2] + "-Different"));
					jobRev.waitForCompletion(true);
					
					job.setJarByClass(mrtgenDriver.class);
					job.setJarByClass(getClass());
					FileInputFormat.addInputPath(job, new Path(args[2] + "-Different"));
					FileOutputFormat.setOutputPath(job, new Path(args[2]));
					job.setOutputKeyClass(Text.class);
					job.setOutputValueClass(Text.class);
					job.setMapperClass(Map.class);
					job.setReducerClass(Reduce.class);
					job.setInputFormatClass(TextInputFormat.class);
					job.setOutputFormatClass(TextOutputFormat.class);
					job.setNumReduceTasks(3);
					
					
					return job.waitForCompletion(true) ? 0 : 1;
				}	
			}
		
		public static void main(String[] args) throws Exception {
			int exitCode = ToolRunner.run(new mrtgenDriver(), args);
			System.exit(exitCode);
			}
	}