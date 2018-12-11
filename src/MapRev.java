/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class MapRev extends Mapper<LongWritable, Text, Text, Text> {
		private Text emptyText = new Text();

		private static final int BUFFER_SIZE = 10000;

		private Set<String> set;
		
		public static enum SetCounters {
			Flushes
		}

		@Override
		public void setup(Context context) {
			set = new HashSet<String>();
		}

		@Override
		public void cleanup(Context context) throws IOException, InterruptedException {
			writeSet(set, context);
		}

		public void writeSet(Set<String> set, Context ctx) throws IOException, InterruptedException {
			for (String val : set) {
				ctx.write(new Text(val), emptyText);
			}
			ctx.getCounter(SetCounters.Flushes).increment(1);
			set.clear();
		}

		public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenizer = new StringTokenizer(line);

			while (tokenizer.hasMoreTokens()) {
				set.add(tokenizer.nextToken());
				if (set.size() > BUFFER_SIZE) {
					writeSet(set, context);
				}
			}
		}
}