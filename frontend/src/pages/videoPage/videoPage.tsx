import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import VideoPlayer from "../../components/UI/VideoPlayer";
import VideoScroll from "../../components/videosScroll";
import { useGetVideoByIdQuery } from "../../store/api/serverApi";
import Loader from "../../components/UI/Loader/Loader";
import styles from "./videoPage.module.scss";

const VideoPage = () => {
  const { id } = useParams();
  const { data, error, isLoading } = useGetVideoByIdQuery(Number(id));

  return (
    <div className={styles.watchVideo}>
      {!data ? (
        <Loader />
      ) : (
        <>
          <VideoPlayer source={data?.path ?? ""} />
          <div className={styles.side}>
            <VideoScroll />
          </div>
        </>
      )}
    </div>
  );
};

export default VideoPage;
