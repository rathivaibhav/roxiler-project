# 1. Build Stage
FROM node:20-alpine as builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

# 2. Serve Stage
FROM nginx:stable-alpine

# Copy the build output
COPY --from=builder /app/build /usr/share/nginx/html

# FIX: Copy our custom Nginx config
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]