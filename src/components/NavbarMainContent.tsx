import React, { FC, useEffect, useState } from "react";
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import { useAppSelector } from "../hooks/redux";
import CButton from "./UI/CButton/CButton";
import IconButton from "./UI/IconButton/IconButton";
import uploadIcon from "../assets/upload-svgrepo-com.svg";
import InlineLoader from "./UI/Loader/InlineLoader";
import { useLocation, useNavigate } from "react-router-dom";
import PaWindow from "./ProfileActionsWindow/paWindow";

interface MainContentProps {
  isSmallScreen: boolean;
  setSearchBarVisible: (state: boolean) => void;
  setIsMenuShown: (state: boolean) => void;
  isMenuShown: boolean;
}

const NavbarMainContent: FC<MainContentProps> = ({
  setSearchBarVisible,
  isSmallScreen,
  isMenuShown,
  setIsMenuShown,
}) => {
  const { user, isLoaded, isError } = useAppSelector((state) => state.auth);
  const [isFocused, setIsFocused] = useState<boolean>(false);
  const [isProfile, setIsProfile] = useState<boolean>(false);
  const location = useLocation();
  const navigate = useNavigate();

  function handleSearchButton() {
    setSearchBarVisible(true);
    setIsMenuShown(false);
  }
  return (
    <>
      <div className="navbar__menu">
        <IconButton
          icon={menuIcon}
          onClick={() => setIsMenuShown(!isMenuShown)}
        />
        <a href="/"><span>Spot</span></a>
      </div>
      <div className="navbar__center">
        {!isSmallScreen ? (
          <div className={["search__bar", isFocused ? "active" : ""].join(" ")}>
            <img src={searchIcon} alt="иконочка))" draggable={false} />
            <input
              type="text"
              spellCheck={false}
              onFocus={() => setIsFocused(true)}
              onBlur={() => setIsFocused(false)}
            />
          </div>
        ) : (
          <IconButton
            icon={searchIcon}
            onClick={() => setSearchBarVisible(true)}
          />
        )}
      </div>
      <div className="btns">
        {!isLoaded && <InlineLoader />}
        {!user && isLoaded && (
          <>
            <CButton
              buttonType="primary"
              onClick={() => navigate("/login", { state: {from: location} })}
            >
              Sign in
            </CButton>
            <CButton
              buttonType="secondary"
              onClick={() => navigate("/register", { state: {from: location} })}
            >
              Sign up
            </CButton>
          </>
        )}
        {user && (
          <>
            <IconButton icon={uploadIcon} onClick={() => navigate("/videos/upload", {state: {from: location}})}/>
            <img
              alt="иконочка))"
              src={user.pfp}
              className="avatar"
              draggable={false}
              onClick={() => {setIsProfile(!isProfile)}}
            />
            <PaWindow user={user} isActive={isProfile}/> 
          </>
        )}
      </div>
    </>
  );
};

export default NavbarMainContent;
