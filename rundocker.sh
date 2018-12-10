#!/bin/bash
echo "# Creating docker network..."
docker network create --driver=bridge --subnet=172.19.0.0/24 --gateway=172.19.0.1 Hadoop
echo "# Running docker..."
docker-compose -f ./docker-compose.yml up
