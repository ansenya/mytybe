import React, { FC, useEffect } from "react";
import styles from "./Notification.module.scss";

interface NotificationProps {
  text: string;
  isCalled: boolean;
  setIsCalled: Function;
  isErrorStyle?: boolean;
}

const NotificationElement: FC<NotificationProps> = ({
  isCalled,
  text,
  setIsCalled,
  isErrorStyle,
}) => {
  useEffect(() => {
    if (isCalled) setTimeout(() => setIsCalled(false), 4000);
  }, [isCalled]);

  return (
    <div
      className={[
        styles.NotificationBlock,
        isCalled ? styles.active : "",
        isErrorStyle ? styles.error : "",
      ].join(" ")}
    >
      {text}
    </div>
  );
};

export default NotificationElement;
