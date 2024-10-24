import React, { useEffect, useState } from "react";
import { IChannel } from "../../models";
import ChannelAvatar from "../../assets/ui.jpg";
import { useNavigate } from "react-router-dom";
import { Link } from "react-router-dom";
import { Trash2 } from "lucide-react";
import DeleteModal from "./Popups/DeleteModal/DeleteModal";
import { useDeleteVideoByIdMutation } from "../../store/api/serverApi";

interface VideoCardProps {
  id: number;
  thumbnail: string;
  name: string;
  channel: IChannel;
  canDelete: boolean;
}

const VideoCard = ({
  id,
  thumbnail,
  name,
  channel,
  canDelete,
}: VideoCardProps) => {
  const [showModal, setShowModal] = useState(false);
  const navigate = useNavigate();

  const [deleteAction, { data, isLoading, isError }] =
    useDeleteVideoByIdMutation();


  useEffect(() => {
    if (data) {
    } 
  }, [isLoading])

  return (
      <div className="video" onClick={() => navigate(`/videos/${id}`)}>
          <DeleteModal
              active={showModal}
              setActive={setShowModal}
              id={id}
              deleteAction={deleteAction}
          />
          {canDelete && (
              <button
                  className="video__delete"
                  onClick={(e) => {
                      setShowModal(true);
                      e.stopPropagation()
                  }}
              >
                  <Trash2 size={20} color={"white"}/>
              </button>
          )}
          <img
              src={thumbnail}
              className="video__thumbnail"
              draggable={false}
              loading="lazy"
          />
          <div className="video__name">
              <img
                  src={channel.chp}
                  className="avatar"
                  draggable={false}
                  onClick={(e) => {
                      e.stopPropagation();
                      navigate(`/channels/${channel.id}`);
                  }}
              />
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
