/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */
 
import java.io.IOException;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class ReduceRev extends Reducer<Text, Text, Text, Text> {
		private Text emptyText = new Text();

		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {

			context.write(key, emptyText);
		}
	}