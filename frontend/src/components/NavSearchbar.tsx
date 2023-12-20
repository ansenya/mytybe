import React, { FC, useEffect, useRef, useState } from "react";
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg";
import IconButton from "./UI/IconButton/IconButton";
import arrowIcon from "../assets/arrow-back-long-svgrepo-com.svg";

interface NavSearchbarProps {
  setSearchBarVisible: (state: boolean) => void;
}

const NavSearchbar: FC<NavSearchbarProps> = ({ setSearchBarVisible }) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const [isFocused, setIsFocused] = useState<boolean>(false);

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
        <div
          className={["search__smartphone", isFocused ? "active" : ""].join(
            " ",
          )}
        >
          <img src={searchIcon} alt="иконочка))" draggable={false} />
          <input
            type="text"
            ref={inputRef}
            spellCheck={false}
            onFocus={() => setIsFocused(true)}
            onBlur={() => setIsFocused(false)}
          />
        </div>
      </div>
    </>
  );
};

export default NavSearchbar;
