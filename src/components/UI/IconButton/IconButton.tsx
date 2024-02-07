import React, { ButtonHTMLAttributes } from "react";
import styles from "./IconButton.module.scss"

interface addIconButtonProps {
  icon: string;
}
type IconButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & addIconButtonProps 

const IconButton = ({icon, ...props}: IconButtonProps) => {
  return (
    <button
      className={styles.iconButton}
      {...props}
    >
      <img src={icon} alt="иконочка))" draggable={false} />
    </button>
  )
}

export default IconButton; 
