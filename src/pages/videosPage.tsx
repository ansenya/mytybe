import React, { useMemo } from "react";
import { useLocation } from "react-router-dom";
import VideoScroll from "../components/videosScroll";

const VideosPage = () => {
  const { search } = useLocation();
  const query = useMemo(() => {
    return new URLSearchParams(search);
  }, [search]);
  return (
    <>
      <VideoScroll />
    </>
  );
};

export default VideosPage;
