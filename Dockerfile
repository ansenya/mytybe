FROM python:3.8-slim

WORKDIR /app
RUN apt-get update && apt-get install -y \
    libopencv-dev \
    python3-opencv \
    libjpeg-dev \
    libpng-dev \
    libtiff-dev \
    libavcodec-dev \
    libavformat-dev \
    libswscale-dev \
    libv4l-dev \
    libxvidcore-dev \
    libx264-dev \
    libgtk-3-dev \
    libatlas-base-dev \
    gfortran \
    && rm -rf /var/lib/apt/lists/*

COPY . .
RUN pip install -r requirements.txt
RUN mkdir videos

EXPOSE 8642

CMD ["python3", "main/main.py"]