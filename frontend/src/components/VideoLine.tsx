import React from 'react';
import {IVideo} from "../models";
import VideoCard from "./UI/VideoCard";

interface VideoLineProps {
    categoryName: string,
    videos: IVideo[]
}

const VideoLine = ({videos, categoryName}: VideoLineProps) => {
    return (
        <div className="content__block">
            {videos.map(video =>
                <VideoCard
                    key={video.id}
                    thumbnail={video.thumbnail.path}
                    name={video.name}
                    channel={video.channel}
                />
            )}
        </div>
    );
};

export default VideoLine;