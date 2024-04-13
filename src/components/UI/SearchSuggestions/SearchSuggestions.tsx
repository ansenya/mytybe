import React, { FC, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../../hooks/redux";
import { IChannel, IVideo } from "../../../models";
import styles from "./SearchSuggestions.module.scss";

interface props {
  videosSuggested: IVideo[];
  channelsSuggested: IChannel[];
  currentInputId: string;
}

const SearchSuggestions: FC<props> = ({
  videosSuggested,
  channelsSuggested,
  currentInputId,
}) => {
  const navigate = useNavigate();
  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const [isHovered, setIsHovered] = useState(false);

  const handleClick = (query: string) => {
    navigate(`results?q=${query}`);
  };

  function showCondition() {
    return (
      (videosSuggested !== undefined || channelsSuggested !== undefined) &&
      ((isFocused && currentInputId === focusTargetId) || isHovered)
    );
  }

  return (
    <div
      className={[styles.container, showCondition() ? styles.opened : ""].join(
        " ",
      )}
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {channelsSuggested.map((channel) => (
        <div
          className={styles.suggestion}
          key={channel.id}
          onClick={() => handleClick(channel.name)}
          onTouchStart={() => handleClick(channel.name)}
        >
          <img className={styles.channelAvatar} src={channel?.chp}/>
          <p>{channel.name}</p>
        </div>
      ))}
      {videosSuggested.map((video) => (
        <div
          className={styles.suggestion}
          key={video.id}
          onClick={() => handleClick(video.name)}
          onTouchStart={() => handleClick(video.name)}
        >
          <p>{video.name}</p>
        </div>
      ))}
    </div>
  );
};

export default SearchSuggestions;
