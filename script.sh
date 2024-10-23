#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 input_video"
    exit 1
fi

input_video="$1"
filename=$(basename -- "$input_video")
filename_no_ext="${filename%.*}"

# Get video height
height=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 "$input_video")

# Create master playlist file
master_playlist="videos/master_${filename_no_ext}.m3u8"
echo "#EXTM3U" > "$master_playlist"

# Function for creating HLS stream and adding to master playlist
create_hls() {
    local resolution=$1
    local bitrate=$2
    local height_val=$3
    local bandwidth=$4

    output_files_prefix="videos/${resolution}_${filename_no_ext}"

    ffmpeg -i "$input_video" \
        -vf "scale='trunc(oh*a/2)*2:${height_val}'" \
        -c:v h264 \
        -b:v "${bitrate}k" \
        -preset veryfast \
        -g 48 \
        -keyint_min 48 \
        -sc_threshold 0 \
        -hls_time 10 \
        -hls_playlist_type vod \
        -hls_segment_filename "${output_files_prefix}_%03d.ts" \
        -hls_list_size 0 \
        -f hls "${output_files_prefix}.m3u8" \
        2> /dev/null

    if [ $? -eq 0 ]; then
        echo "#EXT-X-STREAM-INF:BANDWIDTH=${bandwidth},RESOLUTION=$((${height_val}*16/9))x${height_val},CODECS=\"avc1.42e01e,mp4a.40.2\"" >> "$master_playlist"
        echo "${resolution}_${filename_no_ext}.m3u8" >> "$master_playlist"
        echo "${resolution}p HLS stream completed"
    else
        echo "Error creating ${resolution}p HLS stream"
    fi
}

mkdir -p videos

# Create HLS streams for each supported resolution
if [ "$height" -ge 144 ]; then create_hls "144" "500" 144 500000; fi
if [ "$height" -ge 240 ]; then create_hls "240" "1000" 240 1000000; fi
if [ "$height" -ge 360 ]; then create_hls "360" "2000" 360 2000000; fi
if [ "$height" -ge 480 ]; then create_hls "480" "3000" 480 3000000; fi
if [ "$height" -ge 720 ]; then create_hls "720" "5000" 720 5000000; fi
if [ "$height" -ge 1080 ]; then create_hls "1080" "8000" 1080 8000000; fi

echo "All HLS streams completed. Master playlist is located at $master_playlist."