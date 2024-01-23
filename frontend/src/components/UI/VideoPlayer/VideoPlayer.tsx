import React, { useRef, useState } from 'react';
import ReactPlayer from 'react-player';
import PlayerButton from "./PlayerButton";

interface VideoPlayerProps {
    source: string;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ source }) => {
    const playerRef = useRef<ReactPlayer>(null);

    return (
        <div className="player">
            <ReactPlayer
                className="player__video"
                ref={playerRef}
                url={source}
                width="100%"
                height="auto"
            />
            <div className="timeline"></div>
            <div className="controls">
                <PlayerButton icon={""}></PlayerButton>
                <PlayerButton icon={""}></PlayerButton>
                <PlayerButton icon={""}></PlayerButton>
            </div>
        </div>
    );
};

export default VideoPlayer;