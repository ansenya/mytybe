import React, { FC, useEffect } from "react";
import NavButton from "../NavButton/NavButton";
import styles from "./MenuWindow.module.scss";
import channelsIcon from "../../assets/tv-alt-svgrepo-com.svg";
import usersIcon from "../../assets/people-svgrepo-com.svg";
import subsIcon from "../../assets/playlist-svgrepo-com.svg";
import videoIcon from "../../assets/video-svgrepo-com.svg";

interface MenuWindowProps {
  isShown: boolean;
  setIsShown: (state: boolean) => void;
}

const MenuWindow: FC<MenuWindowProps> = ({ isShown, setIsShown }) => {
  return (
    <>
      <div
        className={[styles.overlay, isShown ? styles.active : ""].join(" ")}
        onClick={() => setIsShown(false)}
      ></div>

      <div
        onClick={(e) => e.stopPropagation()}
        className={[styles.menuWindow, isShown ? styles.open : ""].join(" ")}
      >
        <NavButton icon={videoIcon} to="/">Главная</NavButton>
        <NavButton icon={channelsIcon} to="/channels">Популярные каналы</NavButton>
        <NavButton icon={subsIcon} to="/subscriptions">Подписки</NavButton>
      </div>
    </>
  );
};

export default MenuWindow;
