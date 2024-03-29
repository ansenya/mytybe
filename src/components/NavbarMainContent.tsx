import React, {
  FC,
  useDeferredValue,
  useEffect,
  useId,
  useRef,
  useState,
} from "react";
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
import { useLazyGetVideosQuery } from "../store/api/serverApi";
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
  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const [isProfile, setIsProfile] = useState<boolean>(false);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 200);
  const [fetchSearch, { data, isLoading, error }] =
    useLazyGetVideosQuery();
  const location = useLocation();
  const navigate = useNavigate();

  const inputRef = useRef<HTMLInputElement>(null);
  const inputId = useId();

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Enter" && checkFocus()) {
      event.preventDefault();
      setIsFocused({ isFocused: false, focusTargetId: inputId });
      inputRef.current?.blur();
      navigate(`/?q=${query}`);
    }
  };

  useEffect(() => {
    fetchSearch({
      searchQuery: debouncedQuery,
      page: 0,
      size: 5,
      sort: "desc",
    });
  }, [debouncedQuery]);

  function handleSearchButton() {
    setSearchBarVisible(true);
    setIsMenuShown(false);
  }

  function checkFocus() {
    return isFocused && focusTargetId === inputId;
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
              className={["search__bar", checkFocus() ? "active" : ""].join(
                " ",
              )}
            >
              <img src={searchIcon} alt="иконочка))" draggable={false} />
              <input
                id={inputId}
                type="text"
                spellCheck={false}
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyDown={handleKeyDown}
                onFocus={() =>
                  setIsFocused({ isFocused: true, focusTargetId: inputId })
                }
                autoCorrect="off"
                autoComplete="off"
                onBlur={() =>
                  setIsFocused({ isFocused: false, focusTargetId: inputId })
                }
                ref={inputRef}
              />
            </div>
            <SearchSuggestions
              videosSuggested={data?.content}
              currentInputId={inputId}
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
              onClick={() => navigate("/login", { state: { from: location } })}
              style={{ padding: 0 }}
            >
              Sign in
            </CButton>
            <CButton
              buttonType="secondary"
              onClick={() =>
                navigate("/register", { state: { from: location } })
              }
              style={{ padding: 0 }}
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
