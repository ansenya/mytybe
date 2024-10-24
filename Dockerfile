FROM node:21-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build

FROM node:21-alpine AS production

WORKDIR /app

RUN npm install -g serve

COPY --from=build /app/build ./build

CMD ["serve", "-s", "build"]
