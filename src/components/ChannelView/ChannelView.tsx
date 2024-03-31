import React, { FC, useEffect, useMemo, useRef, useState } from "react";
import { useParams } from "react-router-dom";
import { useAppSelector } from "../../hooks/redux";
import { IChannel } from "../../models";
import {
  useFollowMutation,
  useGetChannelByIdQuery,
} from "../../store/api/serverApi";
import CButton from "../UI/CButton/CButton";
import styles from "./ChannelView.module.scss";
import bigImage from "../../assets/DSC01158_2048x.webp";
import FollowButton from "../../pages/videoPage/followButton";
import { toast } from "sonner";
interface ChannelViewProps {
  channel: IChannel;
}

const ChannelView: FC<ChannelViewProps> = ({ channel }) => {
  const [follow, { isLoading }] = useFollowMutation();

  const [followersAmount, setFollowersAmount] = useState(
    channel.followersAmount,
  );
  const [isFollowed, setIsFollowed] = useState(
    channel.followedByThisUser,
  );

  const followersWord = useMemo(() => {
    if (
      (followersAmount > 10 && followersAmount < 20) ||
      [5, 6, 7, 8, 9, 0].includes(followersAmount % 10)
    )
      return "подписчиков";
    if (followersAmount % 10 === 1) return "подписчик";
    else return "подписчика";
  }, [followersAmount]);

  const { user } = useAppSelector((state) => state.auth);

  function subscribe() {
    if (!user) {
      toast.error("Требуется авторизация");
      return;
    }
    if (!isFollowed) {
      toast.success("Подписка оформлена");
    } else {
      toast("Подписка отменена");
    }
    let operation = isFollowed ? -1 : 1;
    setIsFollowed((prevstate) => !prevstate);
    setFollowersAmount((prevstate) => prevstate + operation);
    follow(channel.id);
  }

  const bannerStyles: React.CSSProperties = {
    backgroundImage: `url(${channel.bbc ?? bigImage})`,
  };
  const [isSpreaded, setIsSpreaded] = useState(false);
  const pRef = useRef<HTMLParagraphElement>(null);

  const [showSpreadButton, setShowSpreadButton] = useState<boolean>();

  function handleResize() {
    if (pRef.current) {
      setShowSpreadButton(
        pRef.current.scrollHeight !== pRef.current.clientHeight,
      );
    }
  }

  useEffect(() => {
    if (pRef.current) {
      setShowSpreadButton(
        pRef.current.scrollHeight !== pRef.current.clientHeight,
      );
    }
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return (
    <>
      <div className={styles.header}>
        <div style={bannerStyles} className={styles.banner}></div>
        <div className={styles.channelInfo}>
          <img src={channel.chp} draggable={false} />
          <div className={styles.textInfo}>
            <div className={styles.channelName}>
              <h1>{channel.name}</h1>
              <div className={styles.countableInfo}>
                <span>
                  {followersAmount} {followersWord}
                </span>
                |<span>{channel.videosAmount} видео</span>
              </div>
            </div>
            <div className={styles.description}>
              <p ref={pRef} className={!isSpreaded ? styles.notSpreaded : ""}>
                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aliquam
                lobortis orci id ante faucibus, iaculis suscipit nulla
                porttitor. Suspendisse potenti. Etiam quis augue nisl. Donec at
                tincidunt velit. Pellentesque libero metus, varius ac sodales
                et, cursus vel sapien. Lorem ipsum dolor sit amet, consectetur
                adipiscing elit. Donec ultrices magna ac massa bibendum, sed
                ultricies nulla dignissim. Quisque tincidunt ac lacus posuere
                convallis.
              </p>
              {showSpreadButton && (
                <span onClick={() => setIsSpreaded((prevstate) => !prevstate)}>
                  {isSpreaded ? "свернуть" : "показать полностью"}
                </span>
              )}
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
    </>
  );
};

export default ChannelView;
