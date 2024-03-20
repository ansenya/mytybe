import React, { FC, useEffect, useState } from "react";
import { IVideo } from "../../../models";
import {
  useDislikeVideoMutation,
  useLikeVideoMutation,
} from "../../../store/api/serverApi";
import styles from "./LikeButton.module.scss";
import likeIcon from "../../../assets/like-svgrepo-com(1).svg";
import likeFilledIcon from "../../../assets/like-svgrepo-com.svg";
import dislikeIcon from "../../../assets/dislike-svgrepo-com(1).svg";
import dislikeFilledIcon from "../../../assets/dislike-svgrepo-com.svg";
import useDebounce from "../../../hooks/useDebounce";
import { useAppSelector } from "../../../hooks/redux";

interface LikeButtonProps {
  video: IVideo;
}

const LikeButton: FC<LikeButtonProps> = ({ video }) => {
  const {user} = useAppSelector(state => state.auth);
  const [likesCount, setLikesCount] = useState<number>(video.likes);
  const [dislikesCount, setDislikesCount] = useState<number>(video.dislikes);
  const [isLiked, setIsLiked] = useState(video.likedByThisUser);
  const [isDisliked, setIsDisliked] = useState(video.dislikedByThisUser);

  const [like, likeResp] = useLikeVideoMutation();
  const [dislike, dislikeResp] = useDislikeVideoMutation();

  function handleLikeClick() {
    if (isDisliked && !isLiked) {
      setIsDisliked(false);
      setDislikesCount((prevstate) => prevstate - 1);
    }
    let operation = isLiked ? -1 : 1;
    setIsLiked((prevstate) => !prevstate);
    setLikesCount((prevstate) => prevstate + operation);
    like(video.id);
  }

  function handleDislikeClick() {
    if (isLiked && !isDisliked) {
      setIsLiked(false);
      setLikesCount((prevstate) => prevstate - 1);
    }
    let operation = isDisliked ? -1 : 1;
    setIsDisliked((prevstate) => !prevstate);
    setDislikesCount((prevstate) => prevstate + operation);
    dislike(video.id);
  }

  return (
    <div className={styles.container}>
      <button
        onClick={() => handleLikeClick()}
        className={[styles.likeButton, isLiked ? styles.active : ""].join(" ")}
      >
        <img src={isLiked ? likeFilledIcon : likeIcon} />
        <p>{likesCount}</p>
      </button>
      <button
        onClick={() => handleDislikeClick()}
        className={[styles.dislikeButton, isDisliked ? styles.active : ""].join(
          " ",
        )}
      >
        <img src={isDisliked ? dislikeFilledIcon : dislikeIcon} />
        <p>{dislikesCount}</p>
      </button>
    </div>
  );
};

export default LikeButton;
