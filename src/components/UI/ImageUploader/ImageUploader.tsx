import React, { FC, useId, useRef, useState } from "react";
import styles from "./ImageUploader.module.scss";
import picImage from "../../../assets/picture-svgrepo-com.svg";
import { fileValidationError } from "../../../pages/uploadPage/validators";
import ErrorMessage from "../ErrorMessage/ErrorMessage";

interface props {
  fileSet: Function;
}

const ImageUploader: FC<props> = ({ fileSet }) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const frameRef = useRef<HTMLDivElement>(null);

  const [isUploaded, setIsUploaded] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const fileUploadHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const file = event.target.files[0];
      if (file) {
        const error = fileValidationError(file, "image");
        setErrorMsg(error);
        if (error) return;
        const reader = new FileReader();
        reader.onloadend = (e) => {
          if (frameRef.current) {
            frameRef.current.style.backgroundImage = `url(${e.target?.result})`;
            frameRef.current.style.backgroundSize = "100% 100%";
            setIsUploaded(true);
            fileSet(file);
          }
        };
        reader.readAsDataURL(file);
      }
    }
  };

  return (
    <div className={styles.wrapper}>
      <div
        className={styles.imageShowFrame}
        ref={frameRef}
        onClick={() => fileInputRef.current?.click()}
      >
        {!isUploaded && (
          <div className={styles.clickable}>
            <input
              type="file"
              style={{ display: "none" }}
              onChange={fileUploadHandler}
              ref={fileInputRef}
            />
            <img src={picImage} draggable={false} />
            <p>Нажмите чтобы загрузить изображание</p>
          </div>
        )}
      </div>
      {!!errorMsg && <ErrorMessage msg={errorMsg} />}
    </div>
  );
};

export default ImageUploader;
