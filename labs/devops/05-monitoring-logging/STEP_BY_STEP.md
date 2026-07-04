# Step-by-Step Monitoring Guide

## 1. Deploy Prometheus with Docker
```powershell
docker run -d --name prometheus -p 9090:9090 prom/prometheus
```

## 2. Deploy Grafana
```powershell
docker run -d --name grafana -p 3000:3000 grafana/grafana
```

## 3. Access Grafana
Open http://localhost:3000 (admin/admin). Add Prometheus data source at http://localhost:9090.

## 4. Deploy ELK Stack
```powershell
docker network create elastic
docker run -d --name es -p 9200:9200 -e "discovery.type=single-node" --net elastic docker.elastic.co/elasticsearch/elasticsearch:8.11.0
docker run -d --name kibana --net elastic -p 5601:5601 docker.elastic.co/kibana/kibana:8.11.0
```

## 5. Deploy Filebeat
```powershell
docker run -d --name filebeat --user root -v /var/log:/var/log docker.elastic.co/beats/filebeat:8.11.0 filebeat -e
```

## 6. Create Alert Rule
```powershell
# Edit prometheus.yml to add alerting rules
# Reload configuration
curl -X POST http://localhost:9090/-/reload
```

## 7. Build Dashboard
```powershell
# In Grafana: Create → Dashboard → Add panel
# Query: rate(http_requests_total[5m])
```

## 8. Set Up Alert Notifications
```powershell
# In Grafana: Alerting → Contact points → Add Slack/PagerDuty
```
