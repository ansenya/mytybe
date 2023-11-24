import React, { FC, useEffect } from "react";
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import { useAppSelector } from "../hooks/redux";
import CButton from "./UI/CButton/CButton";
import IconButton from "./UI/IconButton/IconButton";

interface MainContentProps {
  isSmallScreen: boolean;
  setSearchBarVisible: (state: boolean) => void;
}

const NavbarMainContent: FC<MainContentProps> = ({
  setSearchBarVisible,
  isSmallScreen,
}) => {
  const {user} = useAppSelector(state => state.auth)

  useEffect(() => {
    console.log(user?.name)
  }, [])
  return (
    <>
      <div className="navbar__menu">
        <IconButton icon={menuIcon}/>
        <span>Spot</span>
      </div>
      <div className="navbar__center">
        {!isSmallScreen ? (
          <div className="search__bar">
            <img src={searchIcon} alt="иконочка))" draggable={false} />
            <input type="text" />
          </div>
        ) : (
          <IconButton
            icon={searchIcon}
            onClick={() => setSearchBarVisible(true)}
          />
        )}
      </div>
      <div className="btns">
        <CButton buttonType="primary">Sign in</CButton>
        <CButton buttonType="secondary">Sign up</CButton>
      </div>
    </>
  );
};

export default NavbarMainContent;
