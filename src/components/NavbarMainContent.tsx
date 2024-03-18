import React, {FC, useDeferredValue, useEffect, useRef, useState} from "react";
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import { useAppSelector } from "../hooks/redux";
import CButton from "./UI/CButton/CButton";
import IconButton from "./UI/IconButton/IconButton";
import uploadIcon from "../assets/upload-svgrepo-com.svg";
import InlineLoader from "./UI/Loader/InlineLoader";
import { useLocation, useNavigate } from "react-router-dom";
import PaWindow from "./ProfileActionsWindow/paWindow";
import { Link } from "react-router-dom";
import { useLazyGetSearchedVideosQuery } from "../store/api/serverApi";
import useDebounce from "../hooks/useDebounce";
import { IVideo } from "../models";
import { useActions } from "../hooks/actions";
import SearchSuggestions from "./UI/SearchSuggestions/SearchSuggestions";

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

  const { setIsFocused } = useActions();
  const { isFocused } = useAppSelector((state) => state.focus);
  const [isProfile, setIsProfile] = useState<boolean>(false);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 200);
  const [fetchSearch, { data, isLoading, error }] =
    useLazyGetSearchedVideosQuery();
  const location = useLocation();
  const navigate = useNavigate();

  const inputRef = useRef<HTMLInputElement>(null);

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Enter") {
      event.preventDefault();
      setIsFocused(false);
      let penis = setTimeout(() => inputRef.current?.blur(), 100)
      navigate(`/?q=${query}`);
    }
  };

  useEffect(() => {
    if (debouncedQuery)
      console.log(debouncedQuery)
      fetchSearch({
        searchQuery: debouncedQuery,
        page: 0,
        size: 5,
        sort: "desc",
      });
  }, [debouncedQuery]);

  useEffect(() => {
    if (data) {
    }
  }, [data]);

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
        <Link to="/">
          <span>Spot</span>
        </Link>
      </div>
      <div className="navbar__center">
        {!isSmallScreen ? (
          <div className="search__container">
            <div
              className={["search__bar", isFocused ? "active" : ""].join(" ")}
            >
              <img src={searchIcon} alt="иконочка))" draggable={false} />
              <input
                type="text"
                spellCheck={false}
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                onFocus={() => setIsFocused(true)}
                onBlur={() => setTimeout(() => setIsFocused(false), 100)}
                ref={inputRef}
              />
            </div>
            <SearchSuggestions videosSuggested={data?.content} />
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
              onClick={() => navigate("/login", { state: { from: location } })}
            >
              Sign in
            </CButton>
            <CButton
              buttonType="secondary"
              onClick={() =>
                navigate("/register", { state: { from: location } })
              }
            >
              Sign up
            </CButton>
          </>
        )}
        {user && (
          <>
            <IconButton
              icon={uploadIcon}
              onClick={() =>
                navigate("/videos/upload", { state: { from: location } })
              }
            />
            <img
              alt="иконочка))"
              src={user.pfp}
              className="avatar"
              draggable={false}
              onClick={() => {
                setIsProfile(!isProfile);
              }}
            />
            <PaWindow user={user} isActive={isProfile} />
          </>
        )}
      </div>
    </>
  );
};

export default NavbarMainContent;
