docker build -t itau/transferencia:1.0 .
kubectl get pods | grep transferencia | awk '{ print "kubectl delete pod " $1} ' | bash
