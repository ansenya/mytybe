import React, { FC, useMemo } from "react";
import { IChannel } from "../../../models";
import styles from "./Channel.module.scss";

interface ChannelCardProps{
  channel: IChannel;
}

const ChannelCard: FC<ChannelCardProps> = ({channel}) => {

  const followersWord = useMemo(() => {
    if (
      (channel.followersAmount > 10 && channel.followersAmount < 20) ||
      [2, 3, 4].includes(channel.followersAmount % 10)
    )
      return "подписчика";
    if (channel.followersAmount % 10 === 1) return "подписчик";
    else return "подписчиков";
  }, [channel]);

  
  const bannerStyles: React.CSSProperties = {
    backgroundImage: `url(${channel.bbc})`,
    width: "100%",
    backgroundSize: "cover",
    backgroundPosition: "69%",

  }

  return <div className={styles.card}>
    <div className={styles.banner} style={bannerStyles}></div>
    <div className={styles.secondLine}>
      <img src={channel.chp} draggable={false}/>
      <div className={styles.info}>
        <h1>{channel.name}</h1>
        <p>{channel.followersAmount} {followersWord}</p>
      </div>
    </div>
  </div>
}

export default ChannelCard;
