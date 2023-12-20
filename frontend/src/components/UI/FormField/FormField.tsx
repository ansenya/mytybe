import React, {
  FC,
  InputHTMLAttributes,
  useId,
  useState,
  forwardRef,
  Ref
} from "react";
import IconButton from "../IconButton/IconButton";
import styles from "./FormField.module.scss";
import eyeIcon from "../../../assets/eye-svgrepo-com.svg";
import eyeSlashIcon from "../../../assets/eye-slash-svgrepo-com.svg";

interface FormFieldAdd {
  isPassword: boolean;
  fieldName: string;
}

type FormFieldProps = FormFieldAdd & InputHTMLAttributes<HTMLInputElement>;
const FormField: FC<FormFieldProps> = forwardRef(
  ({ fieldName, isPassword, ...props }, ref: Ref<HTMLInputElement>) => {
    const [focused, setFocused] = useState<boolean>(false);
    const [isHidden, setIsHidden] = useState<boolean>(true);
    const inputId = useId();

    const toCamelCase = (word: string) => word[0].toUpperCase() + word.slice(1);

    return (
      <div className={styles.formField}>
        <label htmlFor={inputId}>{toCamelCase(fieldName)}</label>
        <div
          className={[styles.formInput, focused ? styles.active : ""].join(" ")}
        >
          <input
            {...props}
            ref={ref}
            type={isHidden && isPassword ? "password" : "text"}
            placeholder={`Enter ${toCamelCase(fieldName)}`}
            id={inputId}
          />
          {isPassword && (
            <img
              style={{ maxHeight: 20 }}
              alt="h"
              src={isHidden ? eyeSlashIcon : eyeIcon}
              onClick={() => setIsHidden(!isHidden)}
            />
          )}
        </div>
      </div>
    );
  },
);

export default FormField;
