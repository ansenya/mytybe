import React, { FC, useState } from "react";
import styles from "./PopUpButton.module.scss";
import crossIcon from "../../../assets/cross-svgrepo-com.svg";

interface PopUpButtonProps {
  children?: React.ReactNode;
  icon: string;
  name: string;
}

const PopUpButton: FC<PopUpButtonProps> = ({ name, icon, children }) => {
  const [isOpened, setIsOpened] = useState(false);

  return (
    <>
      <button
        className={styles.buttonTrigger}
        onClick={() => setIsOpened(true)}
      >
        <img src={icon} />
      </button>
      <div
        className={[styles.overlay, isOpened ? styles.active : ""].join(" ")}
        onClick={(e) => e.stopPropagation()}
      >
        <div className={styles.window}>
          <div className={styles.head}>
            <h3>{name}</h3>
            <img src={crossIcon}/>
          </div>
          <div className={styles.body}>{children}</div>
        </div>
      </div>
    </>
  );
};

export default PopUpButton;
