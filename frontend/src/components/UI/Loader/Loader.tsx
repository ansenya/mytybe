import React from "react";
import styles from "./Loader.module.scss"

const Loader = () => {
  return <div style={{width: "100%", display: "flex", justifyContent: "center"}}>
    <div className={styles.loader}></div>;
  </div>;
};

export default Loader;
