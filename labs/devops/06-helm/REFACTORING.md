# Helm Refactoring

## Before (Duplicate Labels in Every Template)
```yaml
# deployment.yaml
metadata:
  labels:
    app: myapp
    chart: mychart-1.0.0
    release: myrelease

# service.yaml
metadata:
  labels:
    app: myapp
    chart: mychart-1.0.0
    release: myrelease
```

## After (Using _helpers.tpl)
```yaml
# _helpers.tpl
{{- define "myapp.labels" -}}
helm.sh/chart: {{ include "myapp.chart" . }}
{{ include "myapp.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{- define "myapp.selectorLabels" -}}
app.kubernetes.io/name: {{ include "myapp.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

# deployment.yaml
metadata:
  labels:
    {{- include "myapp.labels" . | nindent 4 }}
```

## Before (Hardcoded Values)
```yaml
# templates/deployment.yaml
containers:
  - image: nginx:stable
    resources:
      limits:
        cpu: 500m
        memory: 512Mi
```

## After (Parameterized via values.yaml)
```yaml
# values.yaml
image:
  repository: nginx
  tag: stable
resources:
  limits:
    cpu: 500m
    memory: 512Mi

# deployment.yaml
containers:
  - image: "{{ .Values.image.repository }}:{{ .Values.image.tag }}"
    resources:
      {{- toYaml .Values.resources | nindent 12 }}
```

## Gains
- DRY labels via helpers template
- Configurable via values.yaml
- Reduced duplication and improved maintainability
