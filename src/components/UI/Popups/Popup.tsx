import React, { FC, useEffect } from "react";
import styles from "./Popup.module.scss";

interface Props {
  active: boolean;
  setActive: Function;
}

const Popup: FC<React.PropsWithChildren<Props>> = ({children, active, setActive}) => {
  


  return <>
    <div className={[styles.popup, active ? styles.active : ""].join(" ")} onClick={(e) => e.stopPropagation()}>
      <div className={styles.overlay} onClick={() => setActive(false)}></div>
      <div className={styles.popupContent}>
        {children}
      </div>
    </div>
  </>;
}

export default Popup;
