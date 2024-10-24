import React, { useState, useEffect, useRef, useMemo, FC } from "react";
import { useLazyGetVideosQuery } from "../store/api/serverApi";
import { IVideo } from "../models";
import Videos from "./Videos";
import InlineLoader from "./UI/Loader/InlineLoader";
import { useLocation, useParams } from "react-router-dom";
import { VideosRequest } from "../models/VideoModels";
import ShowButton from "./UI/ShowButton/ShowButton";
import { useAppSelector } from "../hooks/redux";

interface VideoScrollProps {
  isSmallScreen?: boolean;
  channelId?: number;
  isEditable?: boolean;
  isNarrow?: boolean;
}

const VideoScroll: FC<VideoScrollProps> = ({
  isSmallScreen,
  channelId,
  isEditable,
  isNarrow,
}) => {
  const { id } = useParams();
  const { search } = useLocation();
  const query = useMemo(() => {
    return new URLSearchParams(search);
  }, [search]);
  const [pageNumber, setPageNumber] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(999);
  const observer = useRef<IntersectionObserver>();
  const divRef = useRef(null);

  let [fetchData, { data, isFetching, error }] = useLazyGetVideosQuery();
  const [videos, setVideos] = useState<IVideo[]>([]);

  const { deletedIds } = useAppSelector((state) => state.deletedVideos);

  useEffect(() => {
    let body: VideosRequest & { searchQuery?: string } = {
      page: pageNumber,
      sort: "desc",
      size: 10,
    };

    if (channelId) body.channelId = channelId;

    //@ts-expect-error
    if (query.get("q")) body.searchQuery = query.get("q");

    fetchData(body);
    
  }, [pageNumber, query.get("q")]);

  useEffect(() => {
    if (isSmallScreen) return;
    if (isFetching || data === undefined) return;
    observer.current?.disconnect();
    observer.current = new IntersectionObserver((entries, observer) => {
      if (entries[0].isIntersecting && totalPages - 1 > pageNumber) {
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
      if (changedQuery) {
        setVideos([...data.content]);
        setChangedQuery(false);
      } else {
        setVideos([...videos, ...data.content]);
      }
      setTotalPages(data.totalPages);
    }
  }, [isFetching, data]);

  const [changedQuery, setChangedQuery] = useState(false);

  useEffect(() => {
    setChangedQuery(true);
  }, [query.get("q")]);

  function filterVideos(video: IVideo) {
    return (
      video.id !== Number(id) &&
      !deletedIds.includes(video.id) &&
      video.qualities.length !== 0
    );
  }

  return (
    <>
      <div>
        <Videos
          videos={videos.filter((video: IVideo) => filterVideos(video))}
          categoryName="fuck"
          isEditable={isEditable || false}
          isNarrow={isNarrow}
        />
        <span style={{ color: "transparent" }}>penis</span>
      </div>
      {isFetching && <InlineLoader />}
      {isSmallScreen || (
        <div
          style={{
            width: "100%",
            height: 20,
            background: "transparent",
            marginTop: 10,
          }}
          ref={divRef}
        ></div>
      )}
      <br />
      {isSmallScreen && totalPages - 1 > pageNumber && (
        <ShowButton
          isWide
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
  );
};

export default VideoScroll;