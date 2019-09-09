docker build -t itau/saldokafka:1.0 .
kubectl get pods | grep saldokafka | awk '{ print "kubectl delete pod " $1} ' | bash
