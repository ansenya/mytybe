import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import VideoPlayer from "../../components/UI/VideoPlayer/VideoPlayer";
import VideoScroll from "../../components/videosScroll";
import { useGetVideoByIdQuery } from "../../store/api/serverApi";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import "./videoPage.scss";

const VideoPage = () => {
  const { id } = useParams();
  const { data, error, isLoading } = useGetVideoByIdQuery(Number(id));

  useEffect(() => {
    console.log();
  }, [data]);

  return (
    <div className="watchVideo">
      {!data || isLoading ? (
        <InlineLoader />
      ) : (
        <>
          <div className="video__content">
            <VideoPlayer source={data?.path ?? ""} />
            <div className="playing__title">
              <h1>{data.name}</h1>
            </div>
            <div className="playing__channel">
              <img src={data.channel.chp} className="avatar" />
              <span>{data.channel.name}</span>
            </div>
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
