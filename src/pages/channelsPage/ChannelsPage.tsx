import React, { useMemo } from "react";
import { useLocation } from "react-router-dom";
import ChannelsPagination from "./ChannelsPagination";

const ChannelsPage = () => {
  const { search } = useLocation();
  const query = useMemo(() => {
    return new URLSearchParams(search);
  }, [search]);
  return (
    <>
      <ChannelsPagination type="channels" />
    </>
  );
};

export default ChannelsPage;
