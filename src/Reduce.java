
/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */

import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class Reduce extends Reducer <Text,Text,Text,Text>{
		
		private Set<String> set = new HashSet<String>();
		
		public static enum UpdateCount{
			  CNT
			 }
		
		public void reduce(Text key,Iterable<Text> values, Context cntxt) throws IOException, InterruptedException {
			
			StringBuilder sb = new StringBuilder();
			Configuration cfg = cntxt.getConfiguration();
			String param = cfg.get("hashedVal");
			
			for(Text val:values)
				set.add(val.toString());
			for(String val:set)
				sb.append(val).append(" ");
			//cntxt.getCounter( Cnt.values()[set.size()] ).increment(1);
			cntxt.getCounter(UpdateCount.CNT).increment(1);
			if(sb.toString().contains(param) || key.toString().contains(param))
				cntxt.write(key, new Text(sb.toString()));
			
			set.clear();
		}
	}