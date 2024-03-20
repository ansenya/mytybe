import React, { FC, useState } from "react";
import PopUpButton from "../UI/PopUpButton/PopUpButton";
import shareIcon from "../../assets/share-2-svgrepo-com.svg";
import copyIcon from "../../assets/copy-svgrepo-com.svg";

import styles from "./SharePopup.module.scss";

const SharePopup = () => {

  const [copied, setCopied] = useState(false);

  function handleCopy(text: string) {
    navigator.clipboard.writeText(text);
    setCopied(false);
  }

  return (
    <>
      <PopUpButton icon={shareIcon} name="Поделиться">
        <div className={styles.container}>
          <span className={styles.link}>{window.location.href}</span>
          <button
            className={styles.copyButton}
            onClick={() => handleCopy(window.location.href)}
            disabled={copied}
          >
            <img src={copyIcon} />
          </button>
        </div>
      </PopUpButton>
    </>
  );
};

export default SharePopup;
