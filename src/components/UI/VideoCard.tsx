import React from "react";
import { IChannel } from "../../models";
import ChannelAvatar from "../../assets/ui.jpg";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";

interface VideoCardProps {
  id: number;
  thumbnail: string;
  name: string;
  channel: IChannel;
  canDelete: boolean;
}

const VideoCard = ({ id, thumbnail, name, channel }: VideoCardProps) => {
  const navigate = useNavigate();
  return (
    <div className="video" onClick={() => navigate(`/videos/${id}`)}>
      <img
        src={thumbnail}
        className="video__thumbnail"
        draggable={false}
        loading="eager"
      />
      <div className="video__name">
        <img src={channel.chp} className="avatar" draggable={false} onClick={(e) => {
          e.stopPropagation()
          navigate(`/channels/${channel.id}`);
        }} />
        <div>
          <span className="title">{name}</span>
          <Link
            to={`/channels/${channel.id}`}
            onClick={(e) => e.stopPropagation()}
            className="channel__name"
          >
            {channel.name}
          </Link>
        </div>
      </div>
    </div>
  );
};

export default VideoCard;
