import React, { FC } from "react";
import styles from "./Notification.module.scss";

interface NotificationProps {
  text: string;
  isCalled: boolean;
}

const Notification: FC<NotificationProps> = ({ isCalled, text }) => {
  return (
    <div
      className={[styles.NotificationBlock, isCalled ? styles.active : ""].join(
        " ",
      )}
    >
      {text}
    </div>
  );
};

export default Notification;
