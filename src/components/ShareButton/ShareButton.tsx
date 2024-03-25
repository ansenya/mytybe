import React, { useState } from "react";
import styles from "./ShareButton.module.scss";
import shareIcon from "../../assets/share-2-svgrepo-com.svg";
import NotificationElement from "../UI/Notification/Notification";

const ShareButton = () => {
  const [isShared, setIsShared] = useState(false);

  function handleShare() {
    setIsShared(true);
    navigator.clipboard.writeText(document.location.href);
  }

  return (
    <>
      <button className={styles.shareButton} onClick={() => handleShare()}>
        <img src={shareIcon} />
      </button>
      <NotificationElement
        isCalled={isShared}
        setIsCalled={setIsShared}
        text={"Ссылка скопирована в буфер обмена"}
      />
    </>
  );
};

export default ShareButton;
