import React from 'react';
import {IVideo} from "../models";
import VideoCard from "./UI/VideoCard";

interface VideosProps {
    categoryName: string,
    videos: IVideo[]
}

const Videos = ({videos, categoryName}: VideosProps) => {


    return (
        <div className="content__block">
            {videos.map(video =>
                <VideoCard
                    key={video.id}
                    thumbnail={video.thumbnail}
                    name={video.name}
                    channel={video.channel}
                />
            )}
        </div>
    );
};

export default Videos;