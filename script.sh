#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 image"
    exit 1
fi

image="$1"
filename=$(basename -- "$image")
filename_no_ext="${filename%.*}"

output="images/${filename_no_ext}.webp"

if convert "$image" -resize x486 - | cwebp -q 40 - -o "$output"; then
    echo "Photo resized and converted successfully: $output"
else
    echo "Error: Failed to resize and convert photo."
    exit 1
fi
