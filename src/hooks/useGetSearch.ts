import React, { useEffect, useState } from "react";
import { IChannel, IVideo } from "../models";
import {
  useLazyGetChannelsQuery,
  useLazyGetVideosQuery,
} from "../store/api/serverApi";

export const useGetSearch = (q: string) => {
  const [fetchChannels, channelsQuery] = useLazyGetChannelsQuery();
  const [fetchVideos, videosQuery] = useLazyGetVideosQuery();

  const [channelsResult, setChannelsResult] = useState<IChannel[]>([]);
  const [videosResult, setVideosResult] = useState<IVideo[]>([]);

  useEffect(() => {
    fetchChannels({
      page: 0,
      size: 2,
      sort: "desc",
      searchQuery: q,
    });

    fetchVideos({
      page: 0,
      size: 5,
      sort: "desc",
      searchQuery: q,
    });
  }, [q]);

  useEffect(() => {
    if (videosQuery.isFetching) return;
    if (videosQuery.data) {
      setVideosResult(videosQuery.data.content);
    }
  }, [videosQuery.isFetching]);

  useEffect(() => {
    if (channelsQuery.isFetching) return;
    if (channelsQuery.data) {
      setChannelsResult(channelsQuery.data.content);
    }
  }, [channelsQuery.isFetching]);

  return {channelsResult, videosResult};
};
