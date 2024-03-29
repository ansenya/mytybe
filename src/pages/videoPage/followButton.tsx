import React, { FC, useEffect, useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";
import CButton from "../../components/UI/CButton/CButton";
import { useAppSelector } from "../../hooks/redux";
import { IChannel } from "../../models";
import {
  useFollowMutation,
  useGetChannelByIdQuery,
} from "../../store/api/serverApi";
import "./videoPage.scss";

interface FollowButtonProps {
  channel: IChannel;
}

const FollowButton: FC<FollowButtonProps> = ({ channel }) => {
  const [follow, followMutation] = useFollowMutation();
  const { data, isLoading, isError } = useGetChannelByIdQuery(channel.id);

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
    }
    else{
      toast.info("Подписка отменена")
    }
    let operation = isFollowed ? -1 : 1;
    setIsFollowed((prevstate) => !prevstate);
    setFollowersAmount((prevstate) => prevstate + operation);
    follow(channel.id);
  }

  return (
    <>
      <div className="channel__info">
        <Link to={`/channels/${channel.id}`} className="channel__info__link">
          {channel.name}
        </Link>
        <span className="channel__info__followers">
          {followersAmount} {followersWord}
        </span>
      </div>

      {!!data && data.user.id !== user?.id && (
        <CButton
          disabled={followMutation.isLoading}
          buttonType={isFollowed ? "secondary" : "primary"}
          onClick={() => {
            subscribe();
          }}
        >
          {isFollowed ? "Отписаться" : "Подписаться"}
        </CButton>
      )}
    </>
  );
};

export default FollowButton;
