import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import ChannelView from "../components/ChannelView/ChannelView";
import InlineLoader from "../components/UI/Loader/InlineLoader";
import VideoScroll from "../components/videosScroll";
import { useAppSelector } from "../hooks/redux";
import { useGetChannelByIdQuery } from "../store/api/serverApi";

const ChannelPage = () => {
  const { id } = useParams();

  const { data, isFetching, isError } = useGetChannelByIdQuery(Number(id), {
    refetchOnMountOrArgChange: true
  });
  const { user } = useAppSelector((state) => state.auth);


  return (
    <div className="channel__page">
      {!data || isFetching ? <InlineLoader /> : (
        <>
          <ChannelView channel={data} />
          <div className="channel__videos">
            <VideoScroll
              channelId={data.id}
              isEditable={user?.id === data.user.id}
            />
          </div>
        </>
      )}
    </div>
  );
};

export default ChannelPage;
