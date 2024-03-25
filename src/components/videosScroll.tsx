import React, { useState, useEffect, useRef, useMemo, FC } from "react";
import {
  useLazyGetSearchedVideosQuery,
  useLazyGetVideosQuery,
} from "../store/api/serverApi";
import { IVideo } from "../models";
import Videos from "./Videos";
import InlineLoader from "./UI/Loader/InlineLoader";
import { useLocation, useParams } from "react-router-dom";
import { VideosRequest } from "../models/VideoModels";
import ShowButton from "./UI/ShowButton/ShowButton";

interface VideoScrollProps {
  isSmallScreen?: boolean;
  channelId?: number;
  isEditable?: boolean;
}

const VideoScroll: FC<VideoScrollProps> = ({ isSmallScreen, channelId, isEditable }) => {
  const { id } = useParams();
  const { search } = useLocation();
  const query = useMemo(() => {
    return new URLSearchParams(search);
  }, [search]);
  const [pageNumber, setPageNumber] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(999);
  const observer = useRef<IntersectionObserver>();
  const divRef = useRef(null);
  let useQuery;

  if (query.get("q")) useQuery = useLazyGetSearchedVideosQuery;
  else useQuery = useLazyGetVideosQuery;

  let [fetchData, { data, isFetching, error }] = useQuery();
  const [videos, setVideos] = useState<IVideo[]>([]);

  useEffect(() => {
    let body: VideosRequest & { searchQuery?: string } = {
      page: pageNumber,
      sort: "desc",
      size: 10,
    };
  

    if (channelId) body.channelId = channelId;

    //@ts-expect-error
    if (query.get("q")) body.searchQuery = query.get("q"); 

    //@ts-expect-error
    fetchData(body);
  }, [pageNumber]);

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
      setVideos([...videos, ...data.content]);
      setTotalPages(data.totalPages);
    }
  }, [isFetching, data]);

  return (
    <>
      <div>
        <Videos
          videos={videos.filter((video: IVideo) => video.id !== Number(id))}
          categoryName="fuck"
          isEditable={isEditable || false}
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
      <br/>
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
