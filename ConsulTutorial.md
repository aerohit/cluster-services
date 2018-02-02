# Server
consul agent -server -bootstrap-expect=1 \
    -data-dir=/tmp/consul -node=agent-one -bind=172.20.20.10 \
    -enable-script-checks=true -config-dir=/etc/consul.d

docker run -d --net=host \
    -e 'CONSUL_LOCAL_CONFIG={"skip_leave_on_interrupt": true}' \
    consul agent -server -bind=<external ip> -retry-join=<root agent ip> -bootstrap-expect=<number of server agents>

# Client
consul agent -data-dir=/tmp/consul -node=agent-two \
    -bind=172.20.20.11 -enable-script-checks=true -config-dir=/etc/consul.d

docker run -d --net=host \
    -e 'CONSUL_LOCAL_CONFIG={"leave_on_terminate": true}' \
    consul agent -bind=<external ip> -retry-join=<root agent ip>


# Setting up a consul cluster using docker
Imagine docker machines:
    - consule-server: which would run consul in server mode
    - n1 and n2: which would run consul in client mode

# Starting a consul server (on machine consul-server)
docker run --name consul-server -d --net=host -e 'CONSUL_LOCAL_CONFIG={"skip_leave_on_interrupt": true}' consul agent -server -bind=192.168.99.100 -bootstrap-expect=1

# Starting a consul client (on n1 and n2)
docker run --name consul-client -d --net=host -e 'CONSUL_LOCAL_CONFIG={"leave_on_terminate": true}' consul agent -bind=192.168.99.101 -retry-join=192.168.99.100
docker run --name consul-client -d --net=host -e 'CONSUL_LOCAL_CONFIG={"leave_on_terminate": true}' consul agent -bind=192.168.99.102 -retry-join=192.168.99.100


# Getting list of nodes
curl localhost:8500/v1/catalog/nodes
# Getting list of services
curl localhost:8500/v1/agent/services

# Registering a service with payload:

curl \
    --request PUT \
    --data @payload.json \
    localhost:8500/v1/agent/service/register

where @payload.json is:
{
  "ID": "redis1",
  "Name": "redis",
  "Port": 8000,
  "EnableTagOverride": false
}

# Listing services:
curl localhost:8500/v1/catalog/service/redis
