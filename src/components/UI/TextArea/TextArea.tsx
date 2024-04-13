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

  useEffect(() => {
    if (!inputLength && areaRef.current) {
      areaRef.current.style.height = "24px";
    }
  }, [inputLength]);

  function inputHandler() {
    if (!areaRef.current) return;
    areaRef.current.style.height = "24px";
    let newHeight = areaRef.current.scrollHeight;
    areaRef.current.style.height = `${newHeight}px`;
  }

  function resizableInputHandler() {
    if (inputRef.current) setInputLength(inputRef.current?.value.length);
    if (areaRef.current) setInputLength(areaRef.current?.value.length);
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
            onInput={(e) => resizableInputHandler()}
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
