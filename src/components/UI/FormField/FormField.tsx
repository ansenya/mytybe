import React, {
  FC,
  InputHTMLAttributes,
  useId,
  useState,
  forwardRef,
  Ref,
  useEffect,
} from "react";
import IconButton from "../IconButton/IconButton";
import styles from "./FormField.module.scss";
import eyeIcon from "../../../assets/eye-svgrepo-com.svg";
import eyeSlashIcon from "../../../assets/eye-slash-svgrepo-com.svg";
import warning from "../../../assets/warning-1-svgrepo-com.svg";

interface FormFieldAdd {
  isPassword?: boolean;
  customPlaceholder: string;
  error?: string;
}

type FormFieldProps = FormFieldAdd & InputHTMLAttributes<HTMLInputElement>;
const FormField: FC<FormFieldProps> = forwardRef(
  (
    { isPassword, customPlaceholder, error, ...props },
    ref: Ref<HTMLInputElement>,
  ) => {
    const [isHidden, setIsHidden] = useState<boolean>(true);

    const toCamelCase = (word: string) => word[0].toUpperCase() + word.slice(1);

    return (
      <div className={styles.wrapper}>
        <div className={styles.formField}>
          <input
            autoComplete={isHidden ? "current-password" : "off"}
            {...props}
            className={error ? styles.error : ""}
            type={isPassword && isHidden ? "password" : "text"}
            placeholder=""
            spellCheck={false}
          />
          <div className={styles.label}>{customPlaceholder}</div>
        </div>
        {!!error && (
          <div className={styles.errorMessage}>
            <img src={warning} draggable={false} />
            <span>{error}</span>
          </div>
        )}
        {isPassword && (
          <div className={styles.showPass}>
            <input type="checkbox" onChange={(e) => setIsHidden(prevstate => !prevstate)} />
            <span>Показать пароль</span>
          </div>
        )}
      </div>
    );
  },
);

export default FormField;
