import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import VideoPlayer from "../../components/UI/VideoPlayer/VideoPlayer";
import VideoScroll from "../../components/videosScroll";
import { useGetVideoByIdQuery } from "../../store/api/serverApi";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import "./videoPage.scss";
import { Link } from "react-router-dom";
import VideoDescription from "../../components/UI/VideoDescription/VideoDescription";
import LikeButton from "../../components/UI/LikeButton/LikeButton";
import ShareButton from "../../components/ShareButton/ShareButton";
import CommentSection from "../../components/CommentSection/CommentSection";
import FollowButton from "./followButton";

const FrameVideoPage = () => {
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
      <div className="video__content">
        {!data || isFetching ? (
            <InlineLoader />
        ) : (
            <VideoPlayer
                video={data}
                frame={true}
            />
        )}
      </div>
  );
};

export default FrameVideoPage;
