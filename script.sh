#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Использование: $0 input_video"
    exit 1
fi

input_video="$1"
filename=$(basename -- "$input_video")
filename_no_ext="${filename%.*}"

# Получаем высоту видео
height=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 "$input_video")

# Функция для создания HLS-потока
create_hls() {
    local resolution=$1
    local bitrate=$2
    local height_val=$3

    ffmpeg -i "$input_video" \
        -vf "scale='trunc(oh*a/2)*2:$height_val'" \
        -c:v h264 \
        -b:v "${bitrate}k" \
        -preset veryfast \
        -g 48 \
        -keyint_min 48 \
        -sc_threshold 0 \
        -hls_time 10 \
        -hls_playlist_type vod \
        -hls_segment_filename "videos/${resolution}_${filename_no_ext}_%03d.ts" \
        -f hls "videos/${resolution}_${filename_no_ext}.m3u8" \
        2> /dev/null

    if [ $? -eq 0 ]; then
        echo "${resolution}p HLS поток завершен"
    else
        echo "Ошибка при создании ${resolution}p HLS"
    fi
}

mkdir -p videos

if [ "$height" -ge 144 ]; then
    create_hls "144" "500" 144
fi

if [ "$height" -ge 240 ]; then
    create_hls "240" "1000" 240
fi

if [ "$height" -ge 360 ]; then
    create_hls "360" "2000" 360
fi

if [ "$height" -ge 480 ]; then
    create_hls "480" "3000" 480
fi

if [ "$height" -ge 720 ]; then
    create_hls "720" "5000" 720
fi

if [ "$height" -ge 1080 ]; then
    create_hls "1080" "8000" 1080
fi

echo "Все HLS потоки завершены."
