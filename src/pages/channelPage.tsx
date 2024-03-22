import React, { useState } from "react";
import { useParams } from "react-router-dom";
import ChannelView from "../components/ChannelView/ChannelView";
import InlineLoader from "../components/UI/Loader/InlineLoader";
import VideoScroll from "../components/videosScroll";
import { useGetChannelByIdQuery } from "../store/api/serverApi";

const ChannelPage = () => {
  const { id } = useParams();

  const { data, isLoading, isError } = useGetChannelByIdQuery(Number(id));

  return (
    <>
      {isLoading && <InlineLoader />}
      {!!data && (
        <>
          <ChannelView channel={data} />
          <VideoScroll channelId={data.id} />
        </>
      )}
    </>
  );
};

export default ChannelPage;
