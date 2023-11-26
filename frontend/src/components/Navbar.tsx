import React, { useEffect, useState } from "react";
import { isTemplateHead } from "typescript";
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import { useActions } from "../hooks/actions";
import { useGetAuthQuery } from "../store/api/serverApi";
import NavbarMainContent from "./NavbarMainContent";
import NavSearchbar from "./NavSearchbar";
import CButton from "./UI/CButton/CButton";
import MenuWindow from "./MenuWindow/MenuWindow";

const Navbar = () => {
  const [isSmallScreen, setIsSmallScreen] = useState<boolean>(
    window.innerWidth < 576,
  );
  const [searchBarVisible, setSearchBarVisible] = useState<boolean>(false);
  const { setUser, setIsLoaded, setIsError } = useActions();
  const { isLoading, data, isError } = useGetAuthQuery();
  const [isMenuShown, setIsMenuShown] = useState<boolean>(false);

  const handleResize = () => {
    setIsSmallScreen(window.innerWidth < 576);
  };

  useEffect(() => {
    if (!isSmallScreen && searchBarVisible) {
      setSearchBarVisible(false);
    }
  }, [isSmallScreen]);

  useEffect(() => {
    if (!isLoading && data) {
      setUser(data[0]);
      setIsError(false);
    }
    setIsError(isError);
    setIsLoaded(!!(data || isError));
  }, [isLoading]);

  useEffect(() => {
    window.addEventListener("resize", handleResize);
    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);
  return (
    <div className="navbar">
      {searchBarVisible && isSmallScreen ? (
        <NavSearchbar setSearchBarVisible={setSearchBarVisible} />
      ) : (
        <NavbarMainContent
          setSearchBarVisible={setSearchBarVisible}
          isSmallScreen={isSmallScreen}
          isMenuShown={isMenuShown}
          setIsMenuShown={setIsMenuShown}
        />
      )}

      <MenuWindow isShown={isMenuShown} setIsShown={setIsMenuShown}/>
    </div>
  );
};

export default Navbar;
