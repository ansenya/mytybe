import { channel } from "diagnostics_channel";
import React, { FC, useEffect, useMemo, useRef, useState } from "react";
import { useLocation } from "react-router-dom";
import ChannelCard from "../../components/UI/ChannelCard/ChannelCard";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import { IChannel } from "../../models";
import { ChannelsRequest } from "../../models/ChannelModels";
import { useLazyGetChannelsQuery } from "../../store/api/serverApi";
import styles from "./ChannelsPage.module.scss";

interface ChannelsPaginationProps {
  type: string;
}

const ChannelsPagination: FC<ChannelsPaginationProps> = ({ type }) => {
  const { search } = useLocation();
  const query = useMemo(() => {
    return new URLSearchParams(search);
  }, [search]);
  const [pageNumber, setPageNumber] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(999);
  const observer = useRef<IntersectionObserver>();
  const divRef = useRef(null);

  let [fetchData, { data, isFetching, error }] = useLazyGetChannelsQuery();
  const [channels, setChannels] = useState<IChannel[]>([]);


  useEffect(() => {
    let body: ChannelsRequest & { searchQuery?: string; isSubs?: boolean } = {
      page: pageNumber,
      sort: "desc",
      size: 10,
    };

    //@ts-expect-error
    if (query.get("q")) body.searchQuery = query.get("q");
    body.isSubs = type === "subscriptions";

    fetchData(body);
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
      setChannels([...channels, ...data.content]);
      setTotalPages(data.totalPages);
    }
  }, [isFetching, data]);

  return (
    <>
      <div className={styles.titleDiv}>
        <h1 className={styles.titleText}>
          {type === "channels" ? "Каналы" : "Подписки"}
        </h1>
      </div>
      <div className={styles.channelCardGrid}>
        {channels.map((channel) => (
          <ChannelCard channel={channel} key={channel.id} />
        ))}
      </div>
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
  );
};

export default ChannelsPagination;
