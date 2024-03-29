import React, { FC, useEffect, useId, useRef, useState } from "react";
import { useAppSelector } from "../../../hooks/redux";
import {
  CommentRequest,
  PostCommentRequest,
} from "../../../models/VideoModels";
import { usePostCommentMutation } from "../../../store/api/serverApi";
import styles from "./CommentArea.module.scss";
import InlineLoader from "../Loader/InlineLoader";
import { useActions } from "../../../hooks/actions";
import { IComment } from "../../../models";
import pfp from "../../../assets/def.png";
import { toast } from "sonner";

interface CommentArea {
  commentId?: number;
  videoId?: number;
  extraAction?: Function;
}

const CommentArea: FC<CommentArea> = ({ commentId, videoId, extraAction }) => {
  const areaRef = useRef<HTMLTextAreaElement>(null);
  const [comment, { data, isLoading, isError, isSuccess }] =
    usePostCommentMutation();

  const { user } = useAppSelector((state) => state.auth);

  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const { setIsFocused } = useActions();
  const { setCommentPost } = useActions();

  const [value, setValue] = useState("");
  const [isForbidden, setIsForbidden] = useState(false);

  const commentInputId = useId();

  const [isOpened, setIsOpened] = useState(false);

  useEffect(() => {
    if (focusTargetId === commentInputId && isFocused) setIsOpened(true);
  }, [isFocused]);

  const sendComment = () => {
    if (!value || !/\S/.test(value)) {
      return;
    }

    if (user === null) {
      toast.error("");
      return;
    }

    const body: PostCommentRequest = {
      text: value,
    };

    if (commentId) body.commentId = commentId;
    if (videoId) body.videoId = videoId;

    comment(body);
    setValue("");
  };

  useEffect(() => {
    if (!isLoading && data) {
      setCommentPost({
        commentToResponseId: commentId || null,
        comment: data || null,
        toDelete: false,
      });

      if (extraAction) extraAction(false);
    }
  }, [isLoading]);

  const cancelComment = () => {
    setValue("");
    areaRef.current?.blur();
    setIsOpened(false);

    if (extraAction) extraAction(false);
  };

  useEffect(() => {
    if (!value && areaRef.current) {
      areaRef.current.style.height = "24px";
    }
  }, [value]);

  function inputHandler() {
    if (!areaRef.current) return;
    areaRef.current.style.height = "24px";
    let newHeight = areaRef.current.scrollHeight;
    areaRef.current.style.height = `${newHeight}px`;
  }
  //
  useEffect(() => {
    if (areaRef.current) {
      areaRef.current.addEventListener("input", inputHandler);
    }

    return () => {
      areaRef.current?.removeEventListener("input", inputHandler);
    };
  }, [areaRef.current]);

  return (
    <div className={styles.commentForm}>
      {isLoading ? (
        <InlineLoader />
      ) : (
        <>
          <div className={styles.firstLine}>
            <img
              src={user?.pfp || pfp}
              className={!!commentId ? styles.smaller : ""}
              draggable={false}
            />
            <div
              className={[
                styles.areaWrapper,
                focusTargetId === commentInputId && isFocused
                  ? styles.focused
                  : "",
              ].join(" ")}
            >
              <textarea
                ref={areaRef}
                onChange={(e) => setValue(e.target.value)}
                value={value}
                onFocus={() =>
                  setIsFocused({
                    isFocused: true,
                    focusTargetId: commentInputId,
                  })
                }
                onBlur={() =>
                  setIsFocused({
                    isFocused: false,
                    focusTargetId: commentInputId,
                  })
                }
                placeholder={"Введите текст"}
              />
            </div>
          </div>
          {(isOpened || !!commentId) && (
            <>
              <div className={styles.secondLine}>
                <button
                  className={styles.cancelButton}
                  onClick={() => cancelComment()}
                >
                  Отмена
                </button>
                <button
                  className={styles.submitButton}
                  onClick={() => sendComment()}
                  disabled={!value || !/\S/.test(value)}
                >
                  {!commentId ? "Оставить комментарий" : "Ответить"}
                </button>
              </div>
            </>
          )}
        </>
      )}
    </div>
  );
};

export default CommentArea;
