import React, { useEffect } from "react";
import VideoScroll from "../components/videosScroll";


const VideosPage = () => {
  
  useEffect(() => {
    document.title = "Spot"
  });

  return <><VideoScroll/></>
};

export default VideosPage;
