import React, { useState } from "react";
import styles from "./ShareButton.module.scss";
import shareIcon from "../../assets/share-2-svgrepo-com.svg";
import { toast } from "sonner";

const ShareButton = () => {
  const [isShared, setIsShared] = useState(false);

  function handleShare() {
    toast("Copied to clipboard");
    navigator.clipboard.writeText(window.location.href);
  }

  return (
    <>
      <button className={styles.shareButton} onClick={() => handleShare()}>
        <img src={shareIcon} />
      </button>
    </>
  );
};

export default ShareButton;
