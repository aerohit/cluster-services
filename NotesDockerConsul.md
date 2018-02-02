# Docker Consul tutorial

nb-consul = n-consul
nb1 = n-1

docker-machine create n-consul --driver virtualbox
docker-machine ssh n-consul
docker run -d --restart always \
    -p 8300:8300 -p 8301:8301 -p 8301:8301/udp -p 8302:8302/udp \
    -p 8302:8302 -p 8400:8400 -p 8500:8500 -p 53:53/udp \
    -h server1 progrium/consul -server -bootstrap -ui-dir /ui -advertise (docker-machine ip n-consul)


docker-machine create -d virtualbox --swarm --swarm-master \
    --swarm-discovery="consul://(docker-machine ip n-consul):8500" \
    --engine-opt="cluster-store=consul://(docker-machine ip n-consul):8500" \
    --engine-opt="cluster-advertise=eth1:2376" n-1

docker-machine create -d virtualbox --swarm  \
    --swarm-discovery="consul://(docker-machine ip n-consul):8500" \
    --engine-opt="cluster-store=consul://(docker-machine ip n-consul):8500" \
    --engine-opt="cluster-advertise=eth1:2376" n-2

docker-machine create -d virtualbox --swarm \
    --swarm-discovery="consul://(docker-machine ip n-consul):8500"  \
    --engine-opt="cluster-store=consul://(docker-machine ip n-consul):8500" \
    --engine-opt="cluster-advertise=eth1:2376" n-3


Switch to n-1 node:

eval (docker-machine env --swarm n-1)

Create network:

docker network create --driver overlay --subnet=10.0.9.0/24 my-net
