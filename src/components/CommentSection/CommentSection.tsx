import React, { FC, useEffect, useRef, useState } from "react";
import { useAppSelector } from "../../hooks/redux";
import { IComment, IVideo } from "../../models";
import { CommentRequest } from "../../models/VideoModels";
import { useLazyGetCommentsQuery } from "../../store/api/serverApi";
import CommentArea from "../UI/CommentArea/CommentArea";
import InlineLoader from "../UI/Loader/InlineLoader";
import ShowButton from "../UI/ShowButton/ShowButton";
import Comment from "./Comment/Comment";
import styles from "./CommentSection.module.scss";

interface CommentSectionProps {
  video: IVideo;
  commentId?: number;
}

const CommentSection: FC<CommentSectionProps> = ({ video, commentId }) => {
  const [fetch, { data, isFetching, isError }] = useLazyGetCommentsQuery();

  const [pageNumber, setPageNumber] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(999);
  const observer = useRef<IntersectionObserver>();
  const divRef = useRef<HTMLDivElement>(null);

  const [comments, setComments] = useState<IComment[]>([]);
  const { commentToResponseId, comment, toDelete } = useAppSelector(
    (state) => state.comment,
  );

  useEffect(() => {
    if (!comment) return;

    if (commentToResponseId && commentId === undefined) {
      for (let i = 0; i < comments.length; i++) {
        if (comments[i].id === commentToResponseId){
          let newNextComments = [commentToResponseId].concat(comments[i].nextComments);
          let commentObject: IComment = {
            ...comments[i],
            nextComments: newNextComments,
          };
          let commentsCopy = comments.filter(com => comments[i].id !== com.id);
          commentsCopy.splice(i, 0, commentObject);
          setComments(commentsCopy);
          return;
        }
      }
    }

    if (commentToResponseId && commentToResponseId !== commentId) return;

    if (toDelete) {
      setComments(comments.filter((com) => comment.id !== com.id));
      return;
    }
    setComments([comment, ...comments]);
  }, [comment]);

  useEffect(() => {
    const body: CommentRequest = {
      videoId: video.id,
      page: pageNumber,
      size: 10,
      sort: "desc",
    };
    if (commentId !== undefined) body.commentId = commentId;
    fetch(body);
  }, [pageNumber]);

  useEffect(() => {
    if (commentId) return;
    if (isFetching || data === undefined) return;
    observer.current?.disconnect();
    observer.current = new IntersectionObserver((entries, observer) => {
      if (entries[0].isIntersecting && totalPages - 1 > pageNumber) {
        console.log(pageNumber, totalPages);
        setPageNumber(pageNumber + 1);
      }
    });
    if (divRef.current) {
      observer.current.observe(divRef.current);
    }
    return () => {
      observer.current?.disconnect();
    };
  }, [isFetching, totalPages]);

  useEffect(() => {
    if (!isFetching && data !== undefined) {
      setComments([
        ...comments,
        ...data.content.filter(
          (com) => !comments.map((comm) => comm.id).includes(com.id),
        ),
      ]);
      setTotalPages(data.totalPages);
    }
  }, [isFetching, data]);

  return (
    <>
      <div className={styles.section}>
        {commentId !== undefined ? (
          <>
            {comments.map((comment) => (
              <Comment
                key={comment.id}
                comment={comment}
                responseToCommentId={commentId}
                video={video}
              />
            ))}
            {isFetching && <InlineLoader />}
            {!!comments.length && totalPages - 1 > pageNumber && (
              <ShowButton
                onClick={() => {
                  if (totalPages - 1 > pageNumber) {
                    setPageNumber((prevstate) => prevstate + 1);
                  }
                }}
                disabled={isFetching || data === undefined}
              >
                еще...
              </ShowButton>
            )}
          </>
        ) : (
          <>
            <h1>Комментарии</h1>
            <CommentArea videoId={video.id} />
            {comments.map((comment) => (
              <Comment key={comment.id} comment={comment} video={video} />
            ))}
            {isFetching && <InlineLoader />}
            <div
              style={{
                width: "100%",
                height: 20,
                background: "transparent",
                marginTop: 10,
              }}
              ref={divRef}
            ></div>
          </>
        )}
      </div>
    </>
  );
};

export default CommentSection;
