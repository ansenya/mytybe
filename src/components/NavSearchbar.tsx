import React, { FC, useEffect, useId, useRef, useState } from "react";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import IconButton from "./UI/IconButton/IconButton";
import arrowIcon from "../assets/arrow-back-long-svgrepo-com.svg";
import { useLazyGetSearchedVideosQuery } from "../store/api/serverApi";
import useDebounce from "../hooks/useDebounce";
import { useLocation, useNavigate } from "react-router-dom";
import { useActions } from "../hooks/actions";
import { useAppSelector } from "../hooks/redux";
import SearchSuggestions from "./UI/SearchSuggestions/SearchSuggestions";

interface NavSearchbarProps {
  setSearchBarVisible: (state: boolean) => void;
}

const NavSearchbar: FC<NavSearchbarProps> = ({ setSearchBarVisible }) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const { setIsFocused } = useActions();
  const { isFocused, focusTargetId } = useAppSelector((state) => state.focus);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 200);
  const [fetchSearch, { data, isLoading, error }] =
    useLazyGetSearchedVideosQuery();
  const navigate = useNavigate();

  const mobileInputId = useId();

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Enter") {
      event.preventDefault();
      setIsFocused({ isFocused: false, focusTargetId: mobileInputId });
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

  useEffect(() => {
    inputRef?.current?.focus();
  }, []);

  function checkFocus() {
    return isFocused && focusTargetId === mobileInputId;
  }

  return (
    <>
      <div className="navbar">
        <IconButton
          icon={arrowIcon}
          onClick={() => setSearchBarVisible(false)}
        />
        <div className="search__smartphone__container">
          <div
            className={[
              "search__smartphone",
              checkFocus() ? "active" : "",
            ].join(" ")}
          >
            <img src={searchIcon} alt="иконочка))" draggable={false} />
            <input
              type="text"
              spellCheck={false}
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              onKeyDown={handleKeyDown}
              onFocus={() =>
                setIsFocused({ isFocused: true, focusTargetId: mobileInputId })
              }
              autoComplete="off"
              onBlur={() =>
                setIsFocused({
                  isFocused: false,
                  focusTargetId: mobileInputId,
                })
              }
              ref={inputRef}
            />
          </div>
          <SearchSuggestions
            videosSuggested={data?.content}
            currentInputId={mobileInputId}
          />
        </div>
      </div>
    </>
  );
};

export default NavSearchbar;
