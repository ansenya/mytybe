import React, { useRef, useState } from 'react';
import ReactPlayer from 'react-player';
import { Player } from 'video-react';
import "node_modules/video-react/dist/video-react.css"; // import css

interface VideoPlayerProps {
    source: string;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ source }) => {
    const playerRef = useRef<ReactPlayer>(null);

    return (
        <div>
            {/* ReactPlayer component for video playback */}
            <ReactPlayer
                ref={playerRef}
                url={source}
                controls
                width="100%"
                height="auto"
            />
        </div>
    );
};

export default VideoPlayer;