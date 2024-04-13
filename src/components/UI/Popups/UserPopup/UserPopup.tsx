import React, { FC, useEffect, useRef, useState } from "react";
import { useAppSelector } from "../../../../hooks/redux";
import { useGetUsersQuery } from "../../../../store/api/serverApi";
import TextArea from "../../TextArea/TextArea";
import Popup from "../Popup";
import styles from "./UserPopup.module.scss";
import { fileValidationError, validateUsername } from "./ValidateUserChange";

interface Props {
  post: Function;
  active: boolean;
  setActive: Function;
}

const UserPopup: FC<Props> = ({ post, active, setActive }) => {
  const { user } = useAppSelector((state) => state.auth);
  const fileRef = useRef<HTMLInputElement>(null);

  const [sourceImage, setSourceImage] = useState(user?.pfp);
  const [imageFile, setImageFile] = useState<File | null>(null);
  useEffect(() => {
    setSourceImage(user?.pfp);
    setImageFile(null);
    setImageErrorMsg("");
  }, [user, active]);




  const [imageErrorMsg, setImageErrorMsg] = useState("");
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


  function handlePost() {
    const postFormData = new FormData();


    if (imageErrorMsg || user?.id === undefined) return;

    if (imageFile !== null) postFormData.append("pfp", imageFile);


    post({id: user?.id, userData: postFormData});
    setActive(false);
  }

  return (
    <>
      <Popup active={active} setActive={setActive}>
        <div className={styles.content}>
          <div className={styles.avatarChange}>
            {user?.pfp && (
              <img src={sourceImage} onClick={() => fileRef.current?.click()} />
            )}
            <h1 onClick={() => fileRef.current?.click()}>Изменить аватар</h1>
            <input
              type="file"
              style={{ display: "none" }}
              ref={fileRef}
              onChange={fileUploadHandler}
            />
            {imageErrorMsg && <h3 className={styles.error}>{imageErrorMsg}</h3>}
          </div>
          <div className={styles.buttons}>
            <button onClick={() => setActive(false)}>Отменить</button>
            <button onClick={() => handlePost()}>Изменить</button>
          </div>
        </div>
      </Popup>
    </>
  );
};

export default UserPopup;
