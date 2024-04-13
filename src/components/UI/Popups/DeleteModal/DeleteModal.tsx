import React, { FC } from "react";
import { useActions } from "../../../../hooks/actions";
import Popup from "../Popup";
import styles from "./DeleteModal.module.scss";

interface Props {
  deleteAction: Function;
  active: boolean;
  setActive: Function;
  id: number;
}

const DeleteModal: FC<Props> = ({ active, setActive, id, deleteAction }) => {
  const { addDeletedVideos } = useActions();

  function handleDelete() {
    deleteAction(id);
    setActive(false);
    addDeletedVideos(id);
  }

  return (
    <>
      <Popup active={active} setActive={setActive}>
        <div className={styles.content}>
          <div className={styles.text}><h2>Подтвердите удаление</h2></div>
          <div className={styles.buttons}>
            <button onClick={() => setActive(false)} className={styles.cancel}>Отменить</button>
            <button onClick={() => handleDelete()} className={styles.delete}>Удалить</button>
          </div>
        </div>
      </Popup>
    </>
  );
};

export default DeleteModal;
