import React, { useEffect, useState } from "react";
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
import ShareButton from "../../components/ShareButton/ShareButton";
import CommentSection from "../../components/CommentSection/CommentSection";
import CButton from "../../components/UI/CButton/CButton";
import FollowButton from "./followButton";

const VideoPage = () => {
  const { id } = useParams();
  const { data, error, isFetching } = useGetVideoByIdQuery(Number(id), {
    refetchOnMountOrArgChange: true,
  });



  const [isSmallScreen, setIsSmallScreen] = useState<boolean>(
    window.innerWidth < 1025,
  );

  function handleResize() {
    setIsSmallScreen(window.innerWidth < 1025);
  }
  useEffect(() => {
    window.addEventListener("resize", handleResize);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

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
      {!data || isFetching ? (
        <InlineLoader />
      ) : (
        <>
          <div className="video__content">
            <VideoPlayer
              source={data?.path ?? ""}
              qValues={data?.qualities ?? []}
            />
            <div className="playing__title">
              <h1>{data.name}</h1>
            </div>
            <div className="second__line">
              <div className="playing__channel">
                <Link to={`/channels/${data.channel.id}`}>
                  <img src={data.channel.chp} className="avatar" />
                </Link>
                {data?.channel && <FollowButton channel={data.channel} />}
              </div>
              <div className="playing__actions">
                <LikeButton video={data} />
                <ShareButton />
              </div>
            </div>
            <VideoDescription video={data} />
            {isSmallScreen || <CommentSection video={data} />}
          </div>
          <div className="side__content">
            <VideoScroll isSmallScreen={isSmallScreen} />
            {isSmallScreen && <CommentSection key={id} video={data} />}
          </div>
        </>
      )}
    </div>
  );
};

export default VideoPage;
