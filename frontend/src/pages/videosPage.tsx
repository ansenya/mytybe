import React, { useEffect, useState, useRef, useCallback } from "react";
import { useAppSelector } from "../hooks/redux";
import {
  useGetVideosQuery,
  useLazyGetVideosQuery,
} from "../store/api/serverApi";
import { IVideo } from "../models";
import Videos from "../components/Videos";
import CButton from "../components/UI/CButton/CButton";
import Loader from "../components/UI/Loader/Loader";

const VideosPage = () => {
  const [pageNumber, setPageNumber] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(999);
  const observer = useRef<IntersectionObserver>();
  const divRef = useRef(null);
  const [fetchData, { data, isFetching, error }] = useLazyGetVideosQuery();
  const [videos, setVideos] = useState<IVideo[]>([]);

  useEffect(() => {
    fetchData({
      page: pageNumber,
      sort: "desc",
      size: 60,
    });
  }, [pageNumber]);

  useEffect(() => {
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
        <Videos videos={videos} categoryName="fuck" />
        <span style={{ color: "transparent" }}>penis</span>
      </div>
      {isFetching && <Loader />}
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
  );
};

export default VideosPage;
