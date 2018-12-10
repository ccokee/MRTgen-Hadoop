#!/bin/bash
echo "Hay que ejecutar en una terminal a parte: ./rundocker.sh"
echo "Presione ENTER para continuar..."
read ent

export PATH="$PATH:/usr/home/hadoop/bin"
export PATH="$PATH:/usr/local/hadoop/bin"

echo "Quieres hacer el export de la jvm? y/N"
read sino 

case "$sino" in
y)
 echo "Que quieres usar:"
 echo "		(1) El comando habitual (readlink -f /usr/bin/java)"
 echo "		(2) Exportar icedtea-bin-8 (Solo alumno)"
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

echo "Elegir metodo de ejecucion:"
echo "		(1) Local"
echo "		(2) Cluster docker"
read execution

case "$execution" in
 1)	
	rm -R ./output
	hadoop fs -conf ./conf/hadoop-local.xml -ls .
	hadoop jar MultiRainbowTableGenerator.jar mrtgen F379EAF3C831B04DE153469D1BEC345E ./input/ ./output

	echo "Resultado de la ejecucion en local..."

	cat ./output/*

	echo "Eliminando la carpeta local de resultados..."

	rm -r ./output
 ;;
 2)
echo "Creando la carpeta input en hdfs..."

hadoop fs -mkdir hdfs://172.19.0.2:8020/input

echo "¿Que quieres copiar al cluster?"
echo "     (1) Directorio de muestra: ./input"
echo "     (2) Directorio bruto: /home/coke/Diccionarios/*.dic"
read inpu

case "$inpu" in
 1)
hadoop fs -copyFromLocal ./input/* hdfs://172.19.0.2:8020/input
;;
 2)
hadoop fs -copuFromLocal /home/coke/Diccionarios/*.dic hdfs://172.19.0.2:8020/input
;;
esac 

echo "Comprueba que todo este copiado y pulsa ENTER"
read pausa 

hadoop fs -conf ./conf/hadoop-docker.xml -ls .
hadoop jar MultiRainbowTableGenerator.jar mrtgen F379EAF3C831B04DE153469D1BEC345E hdfs://172.19.0.2:8020/input hdfs://172.19.0.2/output

echo "Resultado de la ejecucion en cluster: "

hadoop fs -cat hdfs://172.19.0.2:8020/output/*

echo "Eliminando directorio de los datanodes: "

hadoop fs -rm -R hdfs://172.19.0.2:8020/output
;;
esac
