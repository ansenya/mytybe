#!/bin/bash

if [ $# -ne 1 ]; then
    echo "using: $0 input_video"
    return 1
fi

input_video="$1"

filename=$(basename -- "$input_video")
filename_no_ext="${filename%.*}"

height=$(ffprobe -v error -select_streams v:0 -show_entries stream=height -of default=noprint_wrappers=1:nokey=1 "$input_video")

if [ "$height" -ge 144 ]; then
    ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:144'" "vids/${filename_no_ext}_144.mp4" 2> /dev/null
    echo "144 finished"
fi

if [ "$height" -ge 240 ]; then
   ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:240'"  "vids/${filename_no_ext}_240.mp4" 2> /dev/null
   echo "240 finished"
fi

if [ "$height" -ge 360 ]; then
   ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:360'" "vids/${filename_no_ext}_360.mp4" 2> /dev/null
   echo "360 finished"
fi

if [ "$height" -ge 480 ]; then
    ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:480'"  "vids/${filename_no_ext}_480.mp4"  2> /dev/null
    echo "480 finished"
fi

if [ "$height" -ge 720 ]; then
    ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:720'"  "vids/${filename_no_ext}_720.mp4" 2> /dev/null
     echo "720 finished"
fi


if [ "$height" -ge 1080 ]; then
    ffmpeg -i "$input_video" -vf "scale='trunc(oh*a/2)*2:1080'" "vids/${filename_no_ext}_1080.mp4" 2> /dev/null
    echo "1080 finished"
fi
