docker build -t itau/senhakafka:1.0 .
kubectl get pods | grep senhakafka | awk '{ print "kubectl delete pod " $1} ' | bash
