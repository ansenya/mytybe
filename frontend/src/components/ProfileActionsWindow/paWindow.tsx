import React, { FC } from "react";
import { useAppSelector } from "../../hooks/redux";
import { IUser } from "../../models";
import NavButton from "../NavButton/NavButton";
import profileIcon from "../../assets/profile-svgrepo-com.svg"
import logoutIcon from "../../assets/logout-svgrepo-com.svg"
import styles from "./paWindow.module.scss"

interface paWindowProps{
  user: IUser,
  isActive: boolean
}

const PaWindow: FC<paWindowProps> = ({isActive, user}) => {
  return <div onClick={(e) => e.stopPropagation()} className={[styles.paWindow, isActive ? styles.active : ""].join(' ')}>
    <NavButton to={`/user/${user.id}`} icon={profileIcon}>Profile</NavButton>
    <NavButton to={'/logout'} icon={logoutIcon}>Logout</NavButton>
  </div>;
}

export default PaWindow;
