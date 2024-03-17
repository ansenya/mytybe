import React, { useEffect, useMemo, useState } from "react";
import { isError } from "util";
import { IChannel } from "../../../models";
import { useGetUserChannelsQuery } from "../../../store/api/serverApi";
import styles from "./ChannelsSelect.module.scss";
import arrowUp from "../../../assets/arrow-up-svgrepo-com 1.svg";

interface ChannelsSelectProps {
  id: number;
  setChannelId: Function;
}

const ChannelsSelect = ({ id, setChannelId }: ChannelsSelectProps) => {
  const { data, isLoading, isError } = useGetUserChannelsQuery(id);
  const [isLoaded, setIsLoaded] = useState<boolean>(false);
  const [isOpened, setIsOpened] = useState(false);

  const [chosen, setChosen] = useState<IChannel>();

  const optionsArray = useMemo(
    () => data?.content?.filter((ch) => ch.id !== chosen?.id),
    [data?.content, chosen],
  );

  useEffect(() => {
    if (data) {
      setIsLoaded(true);
      setChosen(data.content.at(0));
    }
  }, [isLoading]);

  const openOptions = () => {
    if (isLoaded) {
      setIsOpened((prevState) => !prevState);
    }
  };

  useEffect(() => {
    setChannelId(chosen?.id);
  }, [chosen]);

  const setNewSelected = (channel: IChannel) => {
    setChosen(channel);
    setChannelId(channel.id);
    setIsOpened(false);
  };

  return (
    <>
      <div className={styles.wrapper}>
        <div className={styles.selectBtn} onClick={() => openOptions()}>
          <div>
            <img src={chosen?.chp} draggable={false} />
            <p>{chosen?.name}</p>
          </div>
          <img
            className={[styles.icon, isOpened ? styles.opened : ""].join(" ")}
            src={arrowUp}
            draggable={false}
          />
        </div>
        <div
          className={[styles.dropdown, isOpened ? styles.opened : ""].join(" ")}
        >
          {optionsArray?.map((channel) => (
            <div
              className={styles.option}
              onClick={() => setNewSelected(channel)}
              key={channel.id}
            >
              <img src={channel?.chp} draggable={false} />
              <p>{channel?.name}</p>
            </div>
          ))}
        </div>
      </div>
    </>
  );
};

export default ChannelsSelect;
