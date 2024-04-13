import React, { useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import VideoScroll from "../../components/videosScroll";
import { useGetSearch } from "../../hooks/useGetSearch";
import ChannelsPagination from "../channelsPage/ChannelsPagination";
import styles from "./searchPage.module.scss";

const SearchPage = () => {
  const [searchParams, setSearchParams] = useSearchParams();

  const {channelsResult, videosResult} = useGetSearch(searchParams.get("q") || "");

  return (
    <div className={styles.content}>
      {channelsResult.length !== 0 && (
        <div className={styles.channels}>
          <ChannelsPagination type={"channels"} isSmallScreen />
        </div>
      )}
      <div className={styles.videos}>
        <h1>Видео</h1>
        <VideoScroll/>
      </div>
    </div>
  );
};

export default SearchPage;
