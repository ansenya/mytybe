import React, { FC, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppSelector } from "../../../hooks/redux";
import { IChannel, IVideo } from "../../../models";
import styles from "./SearchSuggestions.module.scss";

interface props {
  videosSuggested?: IVideo[];
  channelsSuggested?: IChannel[];
  currentInputId: string;
}

const SearchSuggestions: FC<props> = ({ videosSuggested, channelsSuggested, currentInputId }) => {
  const navigate = useNavigate();
  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const [isHovered, setIsHovered] = useState(false);

  const handleClick = (query: string) => {
    navigate(`${channelsSuggested?.length != 0 ?  "channels" : "/"}?q=${query}`);
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
      {(videosSuggested || channelsSuggested)?.map((item: IVideo | IChannel) => (
        <div
          className={styles.suggestion}
          key={item.id}
          onClick={() => handleClick(item.name)}
          onTouchStart={() => handleClick(item.name)}
        >
          <p>{item.name}</p>
        </div>
      ))}
    </div>
  );
};

export default SearchSuggestions;
