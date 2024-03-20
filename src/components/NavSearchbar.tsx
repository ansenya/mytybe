import React, { FC, useEffect, useRef, useState } from "react";
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
  const { isFocused } = useAppSelector((state) => state.focus);
  const [query, setQuery] = useState("");
  const debouncedQuery = useDebounce(query, 200);
  const [fetchSearch, { data, isLoading, error }] =
    useLazyGetSearchedVideosQuery();
  const navigate = useNavigate();

  const handleKeyDown = (event: React.KeyboardEvent) => {
    if (event.key === "Enter") {
      event.preventDefault();
      setIsFocused(false);
      let penis = setTimeout(() => inputRef.current?.blur(), 100);
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

  return (
    <>
      <div className="navbar">
        <IconButton
          icon={arrowIcon}
          onClick={() => setSearchBarVisible(false)}
        />
        <div className="search__smartphone__container">
          <div
            className={["search__smartphone", isFocused ? "active" : ""].join(
              " ",
            )}
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
      </div>
    </>
  );
};

export default NavSearchbar;
