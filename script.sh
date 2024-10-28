#!/bin/bash

if [ $# -ne 1 ]; then
    echo "Usage: $0 image"
    exit 1
fi

image="$1"
filename=$(basename -- "$image")
filename_no_ext="${filename%.*}"

output="${filename_no_ext}.webp"

if cwebp -q 70 "$image" -o "images/$output"; then
    echo "Photo converted successfully: $output"
else
    echo "Error: Failed to convert photo."
    exit 1
fi
