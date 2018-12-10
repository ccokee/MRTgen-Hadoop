/*
 * · JLRC Multi Rainbow Table Generator 4 Hadoop
 * · Practice for Distributed Sistems
 * · 2018 Leon
 */

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;

public class Map extends Mapper<LongWritable, Text, Text, Text>{
		
		private Text word = new Text();
		private Text val = new Text();
		
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
		
		
		public void map(LongWritable key, Text value, Context cntxt) throws IOException, InterruptedException {
			String line = value.toString();
			StringTokenizer tokenzr = new StringTokenizer(line);
			
			while(tokenzr.hasMoreTokens()){
				word.set(tokenzr.nextToken());
				val.set(Map.getHASHES(word.toString()));
				cntxt.write(val,word);
			}
		}
	}