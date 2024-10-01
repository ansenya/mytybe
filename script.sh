ffmpeg -i input.mp4 \
-map v:0 -s:v:0 1920x1080 -b:v:0 5000k -c:v h264 -f hls -hls_time 10 -hls_playlist_type vod 1080p.m3u8 \
-map v:0 -s:v:1 1280x720  -b:v:1 3000k -c:v h264 -f hls -hls_time 10 -hls_playlist_type vod 720p.m3u8 \
-map v:0 -s:v:2 854x480   -b:v:2 1000k -c:v h264 -f hls -hls_time 10 -hls_playlist_type vod 480p.m3u8