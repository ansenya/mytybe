import React, { FC, useId, useRef } from "react";
import styles from "./ImageUploader.module.scss";
import picImage from "../../../assets/picture-svgrepo-com.svg";

interface props {
  fileSet: Function;
}

const ImageUploader: FC<props> = ({ fileSet }) => {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const frameRef = useRef<HTMLDivElement>(null);

  const fileUploadHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const file = event.target.files[0];
      if (file) {
        const reader = new FileReader();
        reader.onloadend = (e) => {
          if (frameRef.current) {
            frameRef.current.style.backgroundImage = `url(${e.target?.result})`;
            frameRef.current.style.backgroundSize = "cover";
            fileSet(file);
          }
        };
        reader.readAsDataURL(file);
      }
    }
  };

  return (
    <div className={styles.container}>
      <div
        className={styles.clickable}
        onClick={() => fileInputRef.current?.click()}
      >
        <input
          type="file"
          style={{ display: "none" }}
          onChange={fileUploadHandler}
          ref={fileInputRef}
        />
        <img src={picImage} draggable={false}/>
        <p>Нажмите чтобы загрузить изображание</p>
      </div>
      <div className={styles.imageShowFrame}></div>
    </div>
  );
};

export default ImageUploader;
