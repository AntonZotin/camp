FROM node:24-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build

FROM node:24-alpine AS runner
WORKDIR /app
COPY --from=builder /app/package*.json ./
COPY --from=builder /app/build ./build
COPY --from=builder /app/.svelte-kit ./
RUN npm install -g serve
EXPOSE 4173
CMD ["serve", "-s", "build", "-l", "4173"] 