# Debugging — Kubernetes

## Pod Not Starting

```
Symptom: Pod stuck in Pending/CrashLoopBackOff/ImagePullBackOff
Check:
  kubectl describe pod <pod-name>
  kubectl logs <pod-name> [--previous]

Pending:
  - Insufficient resources: "0/3 nodes are available"
  - PVC not bound: "persistentvolumeclaim not found"
  - Node selector / taint: check node labels/tolerations

CrashLoopBackOff:
  - App exits with error code: check logs
  - OOMKilled: kubectl describe → State → Reason: OOMKilled
  - Health check fails: check probe path and port

ImagePullBackOff:
  - Image doesn't exist / wrong tag
  - Registry credentials missing
  - Image pull secret not configured
```

## Service Not Reachable

```
Symptom: Can't connect to service endpoint
Check:
  1. kubectl get endpoints <service>  # Empty? No matching pods
  2. kubectl get pods --show-labels   # Do labels match service selector?
  3. kubectl describe svc <service>   # Check Endpoints field
  4. kubectl port-forward pod <pod> 8080:8080  # Direct pod access test

Fix:
  - Update service selector to match pod labels
  - Ensure pods are running (not Pending)
  - Check targetPort matches container port
```

## DNS Resolution Failure

```
Symptom: "Connection refused" when using service DNS name
Check:
  kubectl run -it --rm debug --image=nicolaka/netshoot -- /bin/bash
  nslookup java-app-svc
  curl http://java-app-svc:80

If nslookup fails:
  - Check CoreDNS pods: kubectl get pods -n kube-system -l k8s-app=kube-dns
  - Check CoreDNS logs: kubectl logs -n kube-system -l k8s-app=kube-dns
  - Check DNS config in pod: /etc/resolv.conf
```

## Node Not Ready

```
Symptom: Node shows NotReady status
Check:
  kubectl describe node <node-name>
  # Look for Conditions section

Common causes:
  - kubelet not running: ssh to node, systemctl status kubelet
  - Disk pressure: check df -h, clean up images
  - Memory pressure: free -m
  - PID pressure: too many processes
  - Network unavailability: CNI plugin issue
```

## PV/PVC Issues

```
Symptom: Pod stuck Pending with "persistentvolumeclaim not found"
Check:
  kubectl get pv    # Available PVs?
  kubectl get pvc   # Bound?
  kubectl describe pvc <name>

Issues:
  - StorageClass not found
  - PV in wrong availability zone
  - No provisioner configured
  - Volume already bound to another PVC

Fix: 
  kubectl delete pvc <name>  # Recreate with correct StorageClass
  Or: manually create PV matching PVC requirements
```
