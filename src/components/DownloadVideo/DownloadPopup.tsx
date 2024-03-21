import React, { FC } from "react";
import { IVideo } from "../../models";
import PopUpButton from "../UI/PopUpButton/PopUpButton";
import downloadIcon from "../../assets/download-svgrepo-com.svg"; 

interface DownloadPopupProps {
  video: IVideo;
}

const DownloadPopup: FC<DownloadPopupProps> = ({video}) => {
  return <>
    <PopUpButton icon={downloadIcon} name="Скачать"></PopUpButton>
  </>
}

export default DownloadPopup;
