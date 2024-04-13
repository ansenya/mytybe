import React, { FC, useMemo } from "react";
import { Link } from "react-router-dom";
import { IChannel } from "../../../models";
import styles from "./ChannelCard.module.scss";
import bigImage from "../../../assets/DSC01158_2048x.webp"

interface ChannelCardProps {
  channel: IChannel;
}

const ChannelCard: FC<ChannelCardProps> = ({ channel }) => {
  const followersWord = useMemo(() => {
    if (
      (channel.followersAmount > 10 && channel.followersAmount < 20) ||
      [2, 3, 4].includes(channel.followersAmount % 10)
    )
      return "подписчика";
    if (channel.followersAmount % 10 === 1) return "подписчик";
    else return "подписчиков";
  }, [channel]);


  return (
    <Link to={`/channels/${channel.id}`}>
      <div className={styles.card}>
        <div className={styles.secondLine}>
          <img src={channel.chp} draggable={false} />
          <div className={styles.info}>
            <h1>{channel.name}</h1>
            <p>
              {channel.followersAmount} {followersWord}
            </p>
          </div>
        </div>
      </div>
    </Link>
  );
};

export default ChannelCard;
