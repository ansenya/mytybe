import React from 'react';
import {IChannel} from "../../models";
import ChannelAvatar from "../../assets/ui.jpg"
import { useNavigate } from 'react-router-dom';

interface VideoCardProps {
    id: number,
    thumbnail: string;
    name: string;
    channel: IChannel
}

const VideoCard = ({id, thumbnail, name, channel}: VideoCardProps) => {
    const navigate = useNavigate();
    return (
        <div className="video" onClick={() => navigate(`/videos/${id}`)}>

            <img src={thumbnail} alt="vidosik))" className="video__thumbnail" draggable={false} loading="eager"/>
            <div className="video__name">
                <img src={channel.chp} alt="avatarka))" className="avatar" draggable={false} />
                <div>
                    <span className='title'>{name}</span>
                    <span className='channel__name'>{channel.name}</span>
                </div>
            </div>
        </div>
    );
};

export default VideoCard;
