import React, { ButtonHTMLAttributes, FC } from 'react';
import styles from "./ShowButton.module.scss"


type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement>;

const ShowButton: FC<ButtonProps> = ({children, ...props}) => {
    return (
       <button 
        {...props} 
        className={styles.showButton}
       >
        {children}
       </button>
    );
};

export default ShowButton;
