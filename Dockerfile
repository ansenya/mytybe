FROM ubuntu:latest
LABEL authors="senya"

ENTRYPOINT ["top", "-b"]