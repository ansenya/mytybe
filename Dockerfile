FROM python:3.8-slim

# Устанавливаем необходимые пакеты для OpenCV и другие зависимости
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

COPY . /app
WORKDIR /app
RUN pip install -r requirements.txt

CMD ["python3", "main/main.py"]