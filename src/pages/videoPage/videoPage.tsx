import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import VideoPlayer from "../../components/UI/VideoPlayer/VideoPlayer";
import VideoScroll from "../../components/videosScroll";
import { useGetVideoByIdQuery } from "../../store/api/serverApi";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import "./videoPage.scss";
import { Link } from "react-router-dom";
import ChipiChapa from "../../assets/asdf.mp4";
import VideoDescription from "../../components/UI/VideoDescription/VideoDescription";
import LikeButton from "../../components/UI/LikeButton/LikeButton";
import SharePopup from "../../components/PlayerPopups/SharePopup";

const VideoPage = () => {
  const { id } = useParams();
  const { data, error, isLoading } = useGetVideoByIdQuery(Number(id));

  useEffect(() => {
    if (data) {
      document.title = data.name;
    }
    return () => {
      document.title = "Spot";
    };
  }, [data]);

  return (
    <div className="watchVideo" key={id}>
      {!data || isLoading ? (
        <InlineLoader />
      ) : (
        <>
          <div className="video__content">
            <VideoPlayer
              source={data?.path ?? ""}
              qValues={data?.qualities ?? []}
              key={id}
            />
            <div className="playing__title">
              <h1>{data.name}</h1>
            </div>
            <div className="second__line">
              <div className="playing__channel">
                <img src={data.channel.chp} className="avatar" />
                <span>{data.channel.name}</span>
              </div>
              <div className="playing__actions">
                <LikeButton video={data}/>
                <SharePopup/>
              </div>
            </div>
            <VideoDescription video={data} />
          </div>
          <div className="side__content">
            <VideoScroll />
          </div>
        </>
      )}
    </div>
  );
};

export default VideoPage;
