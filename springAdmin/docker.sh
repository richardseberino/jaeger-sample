docker build -t itau/springadmin:1.0 .
kubectl get pods | grep springadmin | awk '{ print "kubectl delete pod " $1} ' | bash
