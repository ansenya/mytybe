import React, { FC, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../../hooks/redux";
import { IVideo } from "../../../models";
import styles from "./SearchSuggestions.module.scss";

interface props {
  videosSuggested?: IVideo[];
  currentInputId: string;
}

const SearchSuggestions: FC<props> = ({ videosSuggested, currentInputId }) => {
  const navigate = useNavigate();
  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const [isHovered, setIsHovered] = useState(false);

  const handleClick = (query: string) => {
    navigate(`/?q=${query}`);
  };

  function showCondition() {
    return (
      videosSuggested !== undefined &&
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
      {videosSuggested?.map((video) => (
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
