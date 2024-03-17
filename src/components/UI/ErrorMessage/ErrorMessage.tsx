import React, { FC } from "react";
import styles from "./ErrorMessage.module.scss"

interface props {
  msg: string;
}

const ErrorMessage: FC<props> = ({msg}) => {
  return <div className={styles.wrapper}>
    <p className={styles.message}>{msg}</p>
  </div>
}

export default ErrorMessage;
