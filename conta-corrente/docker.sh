docker build -t itau/contacorrente:1.0 . 
kubectl get pods | grep contacorrente | awk '{ print "kubectl delete pod " $1} ' | bash
