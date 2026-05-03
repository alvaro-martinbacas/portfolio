# Dockerfile para Frontend Vue.js

FROM node:18-alpine AS build-stage

WORKDIR /app

COPY package*.json ./

# Instalar todas las dependencias (incluyendo devDependencies para el build)
RUN npm ci


COPY . .


RUN npm run build


FROM nginx:1.21-alpine AS production-stage


COPY nginx.conf /etc/nginx/nginx.conf


COPY --from=build-stage /app/dist /usr/share/nginx/html


EXPOSE 80


CMD ["nginx", "-g", "daemon off;"]