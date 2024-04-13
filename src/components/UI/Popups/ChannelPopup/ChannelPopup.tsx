import React, { FC, useEffect, useRef, useState } from "react";
import { useAppSelector } from "../../../../hooks/redux";
import { useGetUsersQuery } from "../../../../store/api/serverApi";
import TextArea from "../../TextArea/TextArea";
import Popup from "../Popup";
import styles from "./ChannelPopup.module.scss";
import {
  fileValidationError,
  lengthValidationError,
} from "../../../../pages/uploadPage/validators";
import chp from "../../../../assets/def.png";

interface Props {
  post: Function;
  active: boolean;
  setActive: Function;
}

const ChannelPopup: FC<Props> = ({ post, active, setActive }) => {
  const fileRef = useRef<HTMLInputElement>(null);
  const bbcRef = useRef<HTMLInputElement>(null);

  const [sourceImage, setSourceImage] = useState("");
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [imageErrorMsg, setImageErrorMsg] = useState("");

  const [name, setName] = useState("");
  const [nameError, setNameError] = useState("");

  const fileUploadHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const file = event.target.files[0];
      if (file) {
        const error = fileValidationError(file, "image");
        setImageErrorMsg(error);
        if (error) return;
        //@ts-ignore
        setSourceImage(URL.createObjectURL(file));
        setImageFile(file);
      }
    }
  };

  const [sourceBBC, setSourceBBC] = useState("");
  const [BBC, setBBC] = useState<File | null>(null);
  const [BBCError, setBBCError] = useState("");

  const bbcUploadHandler = (event: React.ChangeEvent<HTMLInputElement>) => {
    if (event.target.files) {
      const file = event.target.files[0];
      if (file) {
        const error = fileValidationError(file, "image");
        setBBCError(error);
        if (error) return;
        //@ts-ignore
        setSourceBBC(URL.createObjectURL(file));
        setBBC(file);
      }
    }
  };

  const nameInputHandler = (event: React.ChangeEvent<HTMLTextAreaElement>) => {
    setName(event.target.value);
  };

  function handlePost() {
    const postFormData = new FormData();

    const error = lengthValidationError(name, 100);  

    setNameError(error);

    if (error || imageErrorMsg || BBCError) return;

    if (imageFile !== null) postFormData.append("chp", imageFile);
    if (BBC !== null) postFormData.append("BigBlackCock", BBC);
    postFormData.append("name", name);
    post(postFormData);
    setActive(false);
  }

  useEffect(() => {
    setSourceImage("");
    setImageFile(null);
    setImageErrorMsg("");
  }, [active]);

  useEffect(() => {
    setSourceBBC("");
    setBBC(null);
    setBBCError("");
  }, [active]);

  useEffect(() => {
    setName("");
    setImageErrorMsg("");
    setNameError("");
  }, [active]);

  return (
    <>
      <Popup active={active} setActive={setActive}>
        <div className={styles.content}>
          <div className={styles.avatarChange}>
            <img
              src={sourceImage || chp}
              onClick={() => fileRef.current?.click()}
            />
            <h1 onClick={() => fileRef.current?.click()}>Добавить аватар</h1>
            <input
              type="file"
              style={{ display: "none" }}
              ref={fileRef}
              onChange={fileUploadHandler}
            />
            {imageErrorMsg && <h3 className={styles.error}>{imageErrorMsg}</h3>}
          </div>
          <div className={styles.avatarChange}>
            <img
              src={sourceBBC || chp}
              onClick={() => bbcRef.current?.click()}
              className={styles.bbc}
            />
            <h1 onClick={() => bbcRef.current?.click()}>Добавить фон</h1>
            <input
              type="file"
              style={{ display: "none" }}
              ref={bbcRef}
              onChange={bbcUploadHandler}
            />
            {BBCError && <h3 className={styles.error}>{BBCError}</h3>}
          </div>
          <div className={styles.form}>
            <TextArea
              isResizble={false}
              labelName={"Название канала"}
              maxLength={100}
              value={name}
              onChange={(e) => setName(e.target.value)}
            />
            {nameError && <h3 className={styles.error}>{nameError}</h3>}
          </div>
          <div className={styles.buttons}>
            <button onClick={() => setActive(false)}>Отменить</button>
            <button onClick={() => handlePost()}>Создать</button>
          </div>
        </div>
      </Popup>
    </>
  );
};

export default ChannelPopup;
