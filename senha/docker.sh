docker build -t itau/senha:1.0 .
kubectl get pods | grep senha | awk '{ print "kubectl delete pod " $1} ' | bash
