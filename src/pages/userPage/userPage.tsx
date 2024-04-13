import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import { toast } from "sonner";
import ChannelCard from "../../components/UI/ChannelCard/ChannelCard";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import ChannelPopup from "../../components/UI/Popups/ChannelPopup/ChannelPopup";
import UserPopup from "../../components/UI/Popups/UserPopup/UserPopup";
import { useActions } from "../../hooks/actions";
import { useAppSelector } from "../../hooks/redux";
import { IChannel } from "../../models";
import {
  useCreateChannelMutation,
  useGetUserByIdQuery,
  useGetUserChannelsQuery,
  useUpdateUserMutation,
} from "../../store/api/serverApi";
import styles from "./userPage.module.scss";

const UserPage = () => {
  const { id } = useParams();
  const { data, isFetching, isError } = useGetUserByIdQuery(Number(id));
  const userChannels = useGetUserChannelsQuery(Number(id));
  const [channels, setChannels] = useState<IChannel[]>([]);

  useEffect(() => {
    if (userChannels.data) {
      setChannels(userChannels.data.content);
    }
  }, [userChannels.isFetching]);

  const { user } = useAppSelector((state) => state.auth);
  const { setUser } = useActions();

  const [post, updateUserQuery] = useUpdateUserMutation();
  const [isUserChangeForm, setIsUserChangeForm] = useState(false);

  const [createChannel, createChannelQuery] = useCreateChannelMutation();
  const [isCreateChannelForm, setIsCreateChannelForm] = useState(false);

  useEffect(() => {
    if (updateUserQuery.data) {
      setUser(updateUserQuery.data);
      toast.success("Данные обновлены");
    } else if (updateUserQuery.isError) {
      toast.error("Ошибка");
    }
  }, [updateUserQuery.isLoading]);

  useEffect(() => {
    if (createChannelQuery.data) {
      setChannels([createChannelQuery.data, ...channels]);
      toast.success("Канал создан");
    } else if (createChannelQuery.isError) {
      toast.error("Ошибка");
    }
  }, [createChannelQuery.isLoading]);

  return (
    <>
      {isFetching ? (
        <InlineLoader />
      ) : (
        <div className={styles.profile}>
          <UserPopup
            post={post}
            active={isUserChangeForm}
            setActive={setIsUserChangeForm}
          />
          <ChannelPopup
            post={createChannel}
            active={isCreateChannelForm}
            setActive={setIsCreateChannelForm}
          />
          <div className={styles.head}>
            <div className={styles.picture}>
              <img src={user?.id === Number(id) ? user?.pfp : data?.pfp} />
            </div>
            <div className={styles.userInfo}>
              <div className={styles.name}>
                <h1>
                  {user?.id === Number(id) ? user?.username : data?.username}
                </h1>
              </div>
              {user?.id === Number(id) && (
                <div className={styles.changeButton}>
                  <button onClick={() => setIsUserChangeForm(true)}>
                    Редактировать профиль
                  </button>
                </div>
              )}
            </div>
          </div>
          <div className={styles.channelsHead}>
            <h1>Каналы</h1>
            {channels.length < 10 && user?.id === Number(id) && (
              <button onClick={() => setIsCreateChannelForm(true)}>
                Создать
              </button>
            )}
          </div>
          {userChannels.isFetching ? (
            <InlineLoader />
          ) : (
            <div className={styles.channels}>
              {channels.map((channel: IChannel) => (
                <ChannelCard channel={channel} key={channel.id} />
              ))}
            </div>
          )}
        </div>
      )}
    </>
  );
};

export default UserPage;
