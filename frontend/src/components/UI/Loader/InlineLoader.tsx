import React from "react";
import styles from "./Loader.module.scss"

{}

const InlineLoader = () => {
  return <div style={{width: "100%", display: "flex", justifyContent: "center"}}>
    <div className={[styles.loader].join(" ")}></div>
  </div>;
};

export default InlineLoader;
