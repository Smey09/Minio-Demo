# Kubernetes Manifests for MinIO Demo

This folder contains Kubernetes manifests for deploying the MinIO demo application in the `minio-demo` namespace.

## Included manifests

- `namespace.yaml` — creates the `minio-demo` namespace
- `minio-secret.yaml` — stores MinIO credentials
- `minio-pvc.yaml` — allocates persistent storage for MinIO data
- `minio-service.yaml` — exposes MinIO within the cluster
- `minio-deployment.yaml` — deploys the MinIO server
- `filestorage-deployment.yaml` — deploys the Spring Boot file storage service
- `filestorage-service.yaml` — exposes the file storage app internally

## Usage

1. Apply the namespace and MinIO resources:

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/minio-secret.yaml
kubectl apply -f k8s/minio-pvc.yaml
kubectl apply -f k8s/minio-service.yaml
kubectl apply -f k8s/minio-deployment.yaml
```

2. Deploy the file storage application:

```bash
kubectl apply -f k8s/filestorage-deployment.yaml
kubectl apply -f k8s/filestorage-service.yaml
```

3. Verify resources:

```bash
kubectl get all -n minio-demo
```

## Notes

- The `filestorage-deployment.yaml` manifest uses `smey09/minio-demo-filestorage:latest` as a placeholder image. Replace this with your actual container image.
- The file storage service is configured to connect to MinIO via `http://minio-service:9000`.
- If you want external access to the file storage app, add an `Ingress` or change `filestorage-service.yaml` to `type: LoadBalancer`.
