
/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class mrtgen {

	public static enum Cnt{
		
		L0,L1,L2,L3,L4,L5
	}
	
	public static enum setCounters{
		
		NFBuffer
	}
	
	public static String getHASHES(String pw){
		
		byte[] pwAsBytes=pw.getBytes();
		MessageDigest md,sha1,sha256;
		
		try{
			md = MessageDigest.getInstance("MD5");
			md.update(pwAsBytes);
			sha1 = MessageDigest.getInstance("SHA-1");
			sha1.update(pwAsBytes);
			sha256 = MessageDigest.getInstance("SHA-256");
			sha256.update(pwAsBytes);
			
			byte[] MD5digest = md.digest();
			byte[] SHA1digest = sha1.digest();
			byte[] SHA256digest = sha256.digest();
					
			Formatter formatMD5 = new Formatter();
			Formatter formatSHA1 = new Formatter();
			Formatter formatSHA256 = new Formatter();
			
			for(byte b: MD5digest){
				formatMD5.toString();
			}
			for(byte b: SHA1digest){
				formatSHA1.toString();
			}
			for(byte b: SHA256digest){
				formatSHA256.toString();
			}
			
			String result=new String(formatMD5.toString() + " " + formatSHA1.toString() + " " + formatSHA256.toString());
			formatMD5.close();
			formatSHA1.close();
			formatSHA256.close();
			return result;
			
		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
			return "-1";
		}
	}
	
	
	public static class Map extends Mapper<LongWritable, Text, Text, Text>{
		
		private Text word = new Text();
		private Text val = new Text();
		
		public void map(LongWritable key, Text value, Context cntxt) throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenzr = new StringTokenizer(line);
			
			while(tokenzr.hasMoreTokens()){
				word.set(tokenzr.nextToken());
				val.set(mrtgen.getHASHES(word.toString()));
				cntxt.write(val,word);
			}
		}
	}
	
	public static class Reduce extends Reducer <Text,Text,Text,Text>{
		
		private Set<String> set = new HashSet<String>();
		
		public void reduce(Text key,Iterable<Text> values, Context cntxt) throws IOException, InterruptedException {
			
			StringBuilder sb = new StringBuilder();
			Configuration cfg = cntxt.getConfiguration();
			String param = cfg.get("hash");
			
			for(Text val:values)
				set.add(val.toString());
			for(String val:set)
				sb.append(val).append(" ");
			cntxt.getCounter( Cnt.values()[set.size()] ).increment(1);
			
			if(sb.toString().trim().contains(param))
				cntxt.write(key, new Text(sb.toString().trim()));
			
			set.clear();
		}
	}
	
	public static void main(String[] args) throws Exception {
		
		Configuration cfg = new Configuration();
		//cfg.addResource("config.xml");
		if(args.length==3){
			cfg.set("hash", args[0]);
		
			Job job = new Job(cfg, "Hashing");
			job.setJarByClass(mrtgen.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(Text.class);
			job.setMapperClass(Map.class);
			job.setReducerClass(Reduce.class);
			job.setInputFormatClass(TextInputFormat.class);
			job.setOutputFormatClass(TextOutputFormat.class);
			job.setNumReduceTasks(20);
		
			FileInputFormat.addInputPath(job, new Path(args[1]));
			FileOutputFormat.setOutputPath(job, new Path(args[2]));
		
			job.waitForCompletion(true);
		} else {
			System.out.println("Usage: mrtgen [HASH] [/dir/input] [/dir/output] ");
		}
	}
}
