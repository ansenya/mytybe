import React, { ButtonHTMLAttributes, FC } from 'react';
import styles from "./ShowButton.module.scss"

interface AddProps {
  isWide?: boolean;
}

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & AddProps;

const ShowButton: FC<ButtonProps> = ({children, isWide, ...props}) => {
    return (
       <button 
        {...props} 
        className={[styles.showButton, isWide ? styles.wide : ''].join(" ")}
       >
        {children}
       </button>
    );
};

export default ShowButton;
