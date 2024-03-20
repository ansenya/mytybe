import React, { FC, useRef, useState } from "react";
import { IVideo } from "../../../models";
import styles from "./VideoDescripttion.module.scss";

interface VideoDescriptionProps {
  video: IVideo;
}

const VideoDescription: FC<VideoDescriptionProps> = ({ video }) => {
  const [isOpened, setIsOpened] = useState(false);
  const formattedDate = new Date(video.created);

  function handleOpen() {
    if (isOpened) return;
    setIsOpened(true);
  }

  return (
    <div
      className={[styles.wrapper, isOpened ? styles.opened : ""].join(" ")}
      onClick={() => handleOpen()}
    >
      <div className={styles.topInfo}>
        <h1>Просмотры: {video.views}</h1>{" "}
        <h1>
          Загружено:{" "}
          {`${formattedDate.getDate()}.${formattedDate.getMonth()}.${formattedDate.getFullYear()}`}
        </h1>
      </div>
      <div className={styles.descBlock}>
        <p className={styles.descText}>
          {!isOpened ? "ещё..." : video.description}
          {isOpened && (
            <>
              {"\n"}
              <span onClick={() => setIsOpened(false)}>скрыть</span>
            </>
          )}
        </p>
      </div>
    </div>
  );
};

export default VideoDescription;
