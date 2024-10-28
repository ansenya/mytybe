FROM golang:1.23.0-alpine AS builder

WORKDIR /app
COPY go.mod go.sum ./
RUN go mod download

COPY . .

RUN go build -o /build

FROM alpine:latest

RUN apk update && \
    apk add --no-cache libwebp libwebp-tools

COPY --from=builder /build /app/build
COPY script.sh /app

WORKDIR /app

CMD ["/app/build"]

