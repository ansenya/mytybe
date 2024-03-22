import React, { FC, useMemo, useState } from "react";
import { useParams } from "react-router-dom";
import { useAppSelector } from "../../hooks/redux";
import { IChannel } from "../../models";
import {
  useFollowMutation,
  useGetChannelByIdQuery,
} from "../../store/api/serverApi";
import CButton from "../UI/CButton/CButton";
import NotificationElement from "../UI/Notification/Notification";
import styles from "./ChannelView.module.scss";
import bigImage from "../../assets/DSC01158_2048x.webp"
interface ChannelViewProps {
  channel: IChannel;
}

const ChannelView: FC<ChannelViewProps> = ({ channel }) => {
  const [follow, { isLoading }] = useFollowMutation();

  const [followersAmount, setFollowersAmount] = useState(
    channel.followersAmount,
  );
  const [isFollowed, setIsFollowed] = useState(
    channel.followedByThisUser || false,
  );
  const [isUnauthorized, setIsUnauthorized] = useState(false);

  const followersWord = useMemo(() => {
    if (
      (followersAmount > 10 && followersAmount < 20) ||
      [2, 3, 4].includes(followersAmount % 10)
    )
      return "подписчика";
    if (followersAmount % 10 === 1) return "подписчик";
    else return "подписчиков";
  }, [followersAmount]);

  const { user } = useAppSelector((state) => state.auth);

  const [followTriggered, setFollowTriggered] = useState(false);

  function subscribe() {
    if (!user) {
      setIsUnauthorized(true);
      return;
    }
    setFollowTriggered(true);
    let operation = isFollowed ? -1 : 1;
    setIsFollowed((prevstate) => !prevstate);
    setFollowersAmount((prevstate) => prevstate + operation);
    follow(channel.id);
  }

  const bannerStyles: React.CSSProperties = {
    backgroundImage: `url(${bigImage})`,
    width: "100%",
    backgroundSize: "cover",
    backgroundPosition: "69%",
    borderRadius: 10, 
  };

  return (
    <>
      <div className={styles.header}>
        <div style={bannerStyles}></div>
        <div className={styles.channelInfo}>
          <img src={channel.chp} draggable={false} />
          <div className={styles.textInfo}>
            <div className={styles.channelName}>
              <h1>{channel.name}</h1>
            </div>
            <div className={styles.countableInfo}>
              <h2>
                {followersAmount} {followersWord}
              </h2>
              <h2>{channel.videosAmount} видео</h2>
            </div>
            <div className={styles.bottom}>
              {user?.id !== channel.user.id && (
                <CButton
                  buttonType={isFollowed ? "secondary" : "primary"}
                  onClick={() => {
                    subscribe();
                  }}
                >
                  {isFollowed ? "Отписаться" : "Подписаться"}
                </CButton>
              )}
            </div>
          </div>
        </div>
      </div>
      <NotificationElement
        isCalled={isUnauthorized}
        setIsCalled={setIsUnauthorized}
        isErrorStyle
        text={"Требуется авторизация"}
      />
      <NotificationElement
        isCalled={followTriggered}
        setIsCalled={setFollowTriggered}
        text={isFollowed ? "Подписка оформлена" : "Подписка отменена"}
      />
    </>
  );
};

export default ChannelView;
