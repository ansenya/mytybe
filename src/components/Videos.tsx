import React, { useEffect } from "react";
import { IVideo } from "../models";
import VideoCard from "./UI/VideoCard";

interface VideosProps {
  categoryName: string;
  videos: IVideo[];
  isEditable: boolean;
}

const Videos = ({ videos, categoryName, isEditable }: VideosProps) => {

  return (
    <div className="content__block">
      {videos.map((video) => (
        <VideoCard
          id={video.id}
          key={video.id}
          thumbnail={video.thumbnail}
          name={video.name}
          channel={video.channel}
          canDelete={isEditable}
        />
      ))}
    </div>
  );
};

export default Videos;
