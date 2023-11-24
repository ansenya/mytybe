import React, { ButtonHTMLAttributes, FC } from 'react';
import styles from "./CButton.module.scss"

type ButtonType = "primary" | "secondary"

interface addButtonProps {
  buttonType?: ButtonType; 
}

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & addButtonProps;

const CButton: FC<ButtonProps> = ({children, buttonType, ...props}) => {
    return (
       <button 
        {...props} 
        className={[styles.cbutton, buttonType === "primary" ? styles.primary : styles.secondary].join(' ')}
       >
        {children}
       </button>
    );
};

export default CButton;
