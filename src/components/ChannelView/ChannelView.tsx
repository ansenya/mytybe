import React, { FC, useState } from "react";
import { useParams } from "react-router-dom";
import { useAppSelector } from "../../hooks/redux";
import { IChannel } from "../../models";
import { useFollowMutation, useGetChannelByIdQuery } from "../../store/api/serverApi";
import styles from "./ChannelView.module.scss";
interface ChannelViewProps {
  channel: IChannel;
}

const ChannelView: FC<ChannelViewProps> = ({channel}) => {

  const [follow, {isLoading}] = useFollowMutation() 

  const [followersAmount, setFollowersAmount] = useState(channel.followersAmount);
  const [isFollowed, setIsFollowed] = useState(false);
  const [isUnauthorized, setIsUnauthorized] = useState(false);

  const { user } = useAppSelector((state) => state.auth);

  function subscribe() {
    if (!user) {
      setIsUnauthorized(true);
      return;
    }
    let operation = isFollowed ? -1 : 1;
    setIsFollowed((prevstate) => !prevstate);
    setFollowersAmount((prevstate) => prevstate + operation);
    follow(channel.id);
  }

  return (
    <>
      <div className={styles.header}>
        <div className={styles.poster}></div>
        <div className={styles.channelInfo}>
          <img src={channel.chp}/>
          <div className={styles.textInfo}></div> 
        </div>
      </div>
    </>
  );
};

export default ChannelView;
