import React, {
  FC,
  TextareaHTMLAttributes,
  useEffect,
  useRef,
  useState,
} from "react";
import { isRegExp } from "util/types";
import styles from "./TextArea.module.scss";

interface TextAreaAddProps {
  isResizble: boolean;
  labelName: string;
  maxLength: number;
}

type TextAreaProps = TextareaHTMLAttributes<HTMLTextAreaElement> &
  TextAreaAddProps;
const TextArea: FC<TextAreaProps> = ({
  labelName,
  isResizble,
  maxLength,
  ...props
}) => {
  const areaRef = useRef<HTMLTextAreaElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const [inputLength, setInputLength] = useState<number>(0);

  function inputHandler() {
    if (!areaRef.current) return;
    areaRef.current.style.height = "auto";
    areaRef.current.style.height = `${areaRef.current.scrollHeight}px`;

    setInputLength(areaRef.current?.value.length);
  }

  function resizableInputHandler() {
    if (inputRef.current) setInputLength(inputRef.current?.value.length);
  }

  useEffect(() => {
    areaRef.current?.addEventListener("input", inputHandler);
    inputRef.current?.addEventListener("input", inputHandler);

    return () => {
      areaRef.current?.removeEventListener("input", inputHandler);
      inputRef.current?.removeEventListener("input", inputHandler);
    };
  }, []);

  return (
    <div>
      <h1 className={styles.label}>{labelName}</h1>

      <div
        className={[
          styles.container,
          !isResizble ? styles.inputType : "",
          inputLength > maxLength ? styles.tooLong : "",
        ].join(" ")}
      >
        {isResizble ? (
          <textarea
            {...props}
            className={[
              styles.area,
              inputLength > maxLength ? styles.tooLong : "",
            ].join(" ")}
            ref={areaRef}
          />
        ) : (
          <input
            {...(props as React.InputHTMLAttributes<HTMLInputElement>)}
            className={[
              styles.area,
              inputLength > maxLength ? styles.tooLong : "",
            ].join(" ")}
            ref={inputRef}
            onInput={(e) => resizableInputHandler()}
          />
        )}
        <p
          className={[
            styles.letterCount,
            inputLength > maxLength ? styles.tooLong : "",
          ].join(" ")}
        >
          {inputLength}/{maxLength}
        </p>
      </div>
    </div>
  );
};

export default TextArea;
