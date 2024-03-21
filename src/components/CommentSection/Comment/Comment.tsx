import React, { FC, useMemo, useState } from "react";
import { useAppSelector } from "../../../hooks/redux";
import { IChannel, IComment, IVideo } from "../../../models";
import CommentArea from "../../UI/CommentArea/CommentArea";
import ShowButton from "../../UI/ShowButton/ShowButton";
import CommentSection from "../CommentSection";
import styles from "./Comment.module.scss";

import likeIcon from "../../../assets/like-svgrepo-com(1).svg";
import likeFilledIcon from "../../../assets/like-svgrepo-com.svg";
import { Link } from "react-router-dom";
import { useDeleteCommentByIdMutation, useLikeCommentMutation } from "../../../store/api/serverApi";

interface CommentProps {
  comment: IComment;
  responseToCommentId?: number;
  video: IVideo;
}

const Comment: FC<CommentProps> = ({ comment, responseToCommentId, video }) => {
  const { user } = useAppSelector((state) => state.auth);
  const [isOpenedResponses, setIsOpenedResponses] = useState(false);
  const [isOpenedForm, setIsOpenedForm] = useState(false);
  const [likesCount, setLikesCount] = useState<number>(comment.likesAmount);

  const [like, {isLoading}] = useLikeCommentMutation();
  const [deleteComment, {}] = useDeleteCommentByIdMutation();

  const author = useMemo(() => {
    if (!comment.user.channels) return "";
    return comment.user.channels
      .map((channel) => channel.id)
      .includes(comment.channel.id)
      ? "(Автор)"
      : "";
  }, [video, comment]);
  const formattedDate = new Date(comment.created);

  const [isLiked, setIsLiked] = useState(false); 

  function likeAction() {
    let operation = isLiked ? -1 : 1;
    setIsLiked((prevstate) => !prevstate);
    setLikesCount((prevstate) => prevstate + operation);
    like(comment.id);
  }

  return (
    <div className={styles.commentWrapper}>
      <div className={styles.main}>
        <img
          className={[
            styles.pfp,
            !!responseToCommentId ? styles.smallerPfp : "",
          ].join(" ")}
          src={comment.user.pfp}
        />
        <div className={styles.commentView}>
          <div className={styles.top}>
            <Link className={styles.link} to={`user/${comment.user.id}`}>
              {`${comment.user.name}${author}`}
            </Link>
            <span>
              {`${formattedDate.getDate()}.${formattedDate.getMonth()}.${formattedDate.getFullYear()}`}
            </span>
          </div>
          <div className={styles.content}>
            <p>{comment.text}</p>
            {user?.id === comment.user.id && (
              <button className={styles.delete} onClick={() => deleteComment(comment.id)}>
                Удалить
              </button>
            )}
          </div>
          <div className={styles.commentActions}>
            <button
              className={styles.actionButton}
              onClick={() => likeAction()}
              disabled={isLoading}
            >
              <img src={isLiked ? likeFilledIcon : likeIcon} />
            </button>
            <p>{likesCount}</p>
            {!!responseToCommentId || (
              <button
                className={styles.actionButton}
                onClick={() => setIsOpenedForm(true)}
              >
                Ответить
              </button>
            )}
          </div>
        </div>
      </div>
      {!!responseToCommentId || (
        <>
          <div className={styles.responsesWrapper}>
            {isOpenedForm && (
              <CommentArea
                commentId={comment.id}
                extraAction={setIsOpenedForm}
              />
            )}
            {comment.nextComments.length && (
              <>
                <ShowButton
                  onClick={() =>
                    setIsOpenedResponses((prevstate) => !prevstate)
                  }
                >
                  {!isOpenedResponses ? "Показать ответы" : "Скрыть"}
                </ShowButton>
                {isOpenedResponses && (
                  <CommentSection commentId={comment.id} video={video} />
                )}
              </>
            )}
          </div>
        </>
      )}
    </div>
  );
};

export default Comment;
