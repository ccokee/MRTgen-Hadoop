#!/bin/bash
echo "Please make sure you ran ./rundocker.sh in other terminal"
echo "Press ENTER to continue..."
read ent

export PATH="$PATH:/usr/home/hadoop/bin"
export PATH="$PATH:/usr/local/hadoop/bin"

echo "Do you need to export the JAVA_HOME enviroment variable? y/N"
read sino 

case "$sino" in
y)
 echo "Choose one option:"
 echo "		(1) Seriously use this option(readlink -f /usr/bin/java)"
 echo "		(2) Export icedtea-bin-8 (only gentoo users with icedtea)"
 read expor
 case $expor in
  1)
  export JAVA_HOME=$(readlink -f /usr/bin/java | sed "s:bin/java::")
  ;;
  2)
  export JAVA_HOME=/usr/lib/jvm/icedtea-bin-8/
  ;;
  esac
 ;;
N)
 ;;
n)
 ;;
esac

echo "Choose execution method"
echo "		(1) Local"
echo "		(2) Cluster docker"
read execution

case "$execution" in
 1)	
	rm -R ./output
	hadoop fs -conf ./conf/hadoop-local.xml -ls .
	hadoop jar MultiRainbowTableGenerator.jar mrtgen F379EAF3C831B04DE153469D1BEC345E ./input/ ./output

	echo "Results..."

	cat ./output/*

	echo "Removing the output..."

	rm -r ./output
 ;;
 2)
echo "Creating the /input folder in hdfs://172.19.0.2:8020"

hadoop fs -mkdir hdfs://172.19.0.2:8020/input

echo "What do you want to copy to the cluster?"
echo "     (1) Example input: ./input from local"
echo "     (2) Dictionaries: ./dictionaries/*.dic from local"
echo "     (3) Example input: ./input from nodename"
echo " 	   (4) Dictionaries: ./dictionaries/*.dic from nodename"
read inpu

case "$inpu" in
 1)
hadoop fs -copyFromLocal ./input/* hdfs://172.19.0.2:8020/input
sudo hadoop jar MultiRainbowTableGenerator.jar -conf conf/hadoop-docker.xml F379EAF3C831B04DE153469D1BEC345E hdfs://172.19.0.2:8020/input hdfs://172.19.0.2:8020/output
echo "Output:"
sudo hadoop fs -cat htfs://172.19.0.2:8020/output/*
echo "Removing output folder..."
sudo hadoop fs -rm -R hdfs://172.19.0.2:8020/output
;;
 2)
echo "Copying ./dictionaries/*.dic to hdfs..."
hadoop fs -copyFromLocal ./dictionaries/*.dic hdfs://172.19.0.2:8020/input
echo "You can see the copy progress here: http://172.19.0.2:50070"
echo "Wait till the files are copyed and then press ENTER"
udo hadoop jar MultiRainbowTableGenerator.jar -conf conf/hadoop-docker.xml F379EAF3C831B04DE153469D1BEC345E hdfs://172.19.0.2:8020/input hdfs://172.19.0.2:8020/output
echo "Output: "
sudo hadoop fs -cat htfs://172.19.0.2:8020/output/*
echo "Removing output folder..."
sudo hadoop fs -rm -R hdfs://172.19.0.2:8020/output
;;
 3)
echo "Copying sample input to input in hdfs..."
sudo hadoop fs -copyFromLocal ./dictionaries/* hdfs://172.19.0.2:8020/input
docker cp ./MultiRainbowTableGenerator.jar hadoop-nn:/
sudo docker exec -i -t hadoop-nn hadoop jar MultiRainbowTableGenerator.jar F379EAF3C831B04DE153469D1BEC345E hdfs://hadoop-nn:8020 hdfs://hadoop-nn:8020/output
sudo docker exec -i -t hadoop-nn /bin/bash
echo "Output:"
sudo docker exec -i -t hadoop-nn hdfs dfs -cat hdfs://hadoop-nn:8020/output/*
echo "Removing output folder..."
sudo docker exec -i -t hadoop-nn hadoop fs -rm -R hdfs://hadoop-nn:8020/output
;;
 4)
echo "Copying ./dictionaries to /input in hdfs..."
sudo hadoop fs -copyFromLocal ./dictionaries/* hdfs://172.19.0.2:8020/input
echo "You can see the copy progress here: http://172.19.0.2:50070"
echo "Wait till the files are copyed and then press ENTER"
docker cp ./MultiRainbowTableGenerator.jar hadoop-nn:/
sudo docker exec -i -t hadoop-nn hadoop jar MultiRainbowTableGenerator.jar F379EAF3C831B04DE153469D1BEC345E hdfs://hadoop-nn:8020 hdfs://hadoop-nn:8020/output
sudo docker exec -i -t hadoop-nn /bin/bash
echo "Output:"
sudo docker exec -i -t hadoop-nn hdfs dfs -cat hdfs://hadoop-nn:8020/output/*
echo "Removing output folder..."
sudo docker exec -i -t hadoop-nn hadoop fs -rm -R hdfs://hadoop-nn:8020/output
;;
esac 
esac
