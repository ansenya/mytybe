import React, { useEffect, useRef, CSSProperties } from "react";
import videojs from "video.js";
import "video.js/dist/video-js.css";
import styles from "../../pages/videoPage/videoPage.module.scss";
import video from "../../assets/asdf.mp4";

const VideoPlayer = ({ source }: { source: string }) => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    const player = videojs(videoRef.current || "");
    player.src({
      src: source,
      type: "video/mp4",
    });
    return () => {
      player.dispose();
    };
  }, [source]);

  const containerStyle: CSSProperties = {
    borderRadius: 10,
    width: "100%",
  };

  return (
    <div data-vjs-player style={containerStyle}>
      <video
        ref={videoRef}
        controls
        className="video-js"
      />
    </div>
  );
};

export default VideoPlayer;
