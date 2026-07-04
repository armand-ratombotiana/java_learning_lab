# Docker Code Deep Dive

## Example Dockerfile (Node.js App)
```dockerfile
FROM node:18-alpine AS base
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM base AS build
RUN npm install
COPY . .
RUN npm run build

FROM nginx:alpine AS production
COPY --from=build /app/dist /usr/share/nginx/html
EXPOSE 80
HEALTHCHECK --interval=30s CMD wget -qO- http://localhost || exit 1
```

## Docker Compose Example
```yaml
version: '3.8'
services:
  db:
    image: postgres:15
    volumes:
      - pgdata:/var/lib/postgresql/data
    environment:
      POSTGRES_PASSWORD: ${DB_PASSWORD}
  app:
    build: .
    ports:
      - "3000:3000"
    depends_on:
      - db
volumes:
  pgdata:
```

## Multi-stage Build Explanation
- Stage 1 (`base`): Install production dependencies
- Stage 2 (`build`): Build application artifacts
- Stage 3 (`production`): Copy only artifacts to minimal nginx image
- Final image size: ~23MB vs ~300MB if done in a single stage
