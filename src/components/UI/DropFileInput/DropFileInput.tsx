import React, {
  FC,
  InputHTMLAttributes,
  MouseEventHandler,
  useRef,
  useState,
} from "react";
import styles from "./DropFileInput.module.scss";
import uploadImage from "../../../assets/upload-svgrepo-com.svg";
import tickImage from "../../../assets/tick-checkbox-svgrepo-com.svg";

interface FileInputAddProps {
  fileSet: Function;
}

type FormFieldProps = FileInputAddProps & InputHTMLAttributes<HTMLInputElement>;
const DropFileInput: FC<FormFieldProps> = ({ fileSet, ...props }) => {
  const inputRef = useRef<HTMLInputElement>(null);
  const [drag, setDrag] = useState<boolean>(false);
  const [uploaded, setUploaded] = useState<boolean>(false);

  const cancelHandler = () => {
    fileSet(null);
    setUploaded(false);
  };

  const clickHandler = (e: React.MouseEvent<HTMLSpanElement>) => {
    if (inputRef.current) {
      inputRef.current.click();
    }
  };

  const onChangeHandler = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      fileSet(e.target?.files[0]);
      setUploaded(true);
    }
  };

  const onStartDragHandler = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDrag(true);
  };

  const onDragLeaveHandler = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    e.stopPropagation();
    setDrag(false);
  };

  const onDropHandler = (e: React.DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    let files = Array.from(e.dataTransfer.files);
    fileSet(files[0]);
    setUploaded(true);
    setDrag(false);
  };

  return (
    <div>
      <h1 className={styles.label}>{"Видеофайл*"}</h1>
      <div
        className={[
          styles.dropField,
          uploaded ? styles.active : "",
          drag ? styles.draggedOver : "",
        ].join(" ")}
        onDragStart={onStartDragHandler}
        onDragOver={onStartDragHandler}
        onDragLeave={onDragLeaveHandler}
        onDrop={onDropHandler}
      >
        <div className={styles.content}>
          <input
            type="file"
            style={{ display: "none" }}
            ref={inputRef}
            onChange={onChangeHandler}
          />
          {!uploaded ? (
            <>
              <img src={uploadImage} alt="upload" draggable={false} />
              <p className={styles.contentText}>
                Перетащите файл или
                <span className={styles.uploadLink} onClick={clickHandler}>
                  {" "}
                  выберите
                </span>
              </p>
            </>
          ) : (
            <>
              <img src={tickImage} alt="upload" draggable={false} />
              <p className={styles.contentText}>
                Загружено.
                <span className={styles.uploadLink} onClick={cancelHandler}>
                  {" "}
                  отменить
                </span>
              </p>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default DropFileInput;
