import React, { useEffect } from "react";
import { useParams } from "react-router-dom";
import VideoPlayer from "../../components/UI/VideoPlayer/VideoPlayer";
import VideoScroll from "../../components/videosScroll";
import { useGetVideoByIdQuery } from "../../store/api/serverApi";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import styles from "./videoPage.module.scss";

const VideoPage = () => {
  const { id } = useParams();
  const { data, error, isLoading } = useGetVideoByIdQuery(Number(id));

  useEffect(() => {
    if (data){
      document.title = data.name
    }
    console.log()
  }, [data]);

  return (
    <div className={styles.watchVideo}>
      {!data || isLoading ? (
        <InlineLoader />
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
