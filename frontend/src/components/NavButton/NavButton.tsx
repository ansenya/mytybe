import React, { FC, useEffect } from "react";
import { useMatch, useNavigate } from "react-router-dom";
import styles from "./NavButton.module.scss";

interface NavButtonProps {
  children: React.ReactNode;
  to: string;
  icon: string;
}

const NavButton: FC<NavButtonProps> = ({ to, children, icon }) => {
  const match = useMatch(to);
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate(to)}
      className={[styles.navButton, match ? styles.active : ""].join(" ")}
    >
      <img src={icon} alt="none"/>
      {children}
    </button>
  );
};

export default NavButton;
