import React, { useId, useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./uploadPage.scss";
import closeIcon from "../../assets/arrow-to-right-svgrepo-com.svg";
import IconButton from "../../components/UI/IconButton/IconButton";
import CButton from "../../components/UI/CButton/CButton";
import {
  useGetUserChannelsQuery,
  useUploadVideoMutation,
} from "../../store/api/serverApi";
import { useAppSelector } from "../../hooks/redux";
import { SubmitHandler, useForm } from "react-hook-form";
import { PaginationResponse, UploadRequest } from "../../models/VideoModels";
import { IChannel } from "../../models";
import DropFileInput from "../../components/UI/DropFileInput/DropFileInput";
import TextArea from "../../components/UI/TextArea/TextArea";
import ImageUploader from "../../components/UI/ImageUploader/ImageUploader";

type UploadForm = Omit<UploadRequest, "channelId">;

const UploadPage = () => {
  const formId = useId();
  const navigate = useNavigate();
  const location = useLocation();
  const [post, { data, isLoading, isError, error }] = useUploadVideoMutation();
  const { user, isLoaded } = useAppSelector((state) => state.auth);
  const [videoFile, setVideoFile] = useState<File | null>(null);
  const [thumbFile, setThumbFile] = useState<File | null>(null);
  const [videoDescription, setVideoDescription] = useState<string>("");
  const [videoName, setVideoName] = useState<string>("")
  const channelsQuery = useGetUserChannelsQuery(user?.id || 0, {
    skip: !isLoaded,
  });

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<UploadForm>();

  const [selectValue, setSelectValue] = useState<[number, string]>([
    0,
    "Select Channel",
  ]);

  const submit: SubmitHandler<UploadForm> = (data) => {
    post({ ...data, channelId: selectValue[0] });
  };

  useEffect(() => {
    if (data) {
      navigate(location.state?.from || "/");
    }
  }, [data]);

  return (
    <>
      <div className="overlay">
        <div className="frame">
          <div className="frame__head">
            <h1>Загрузить видео</h1>
            <IconButton
              onClick={() => navigate(location.state?.from || "/")}
              icon={closeIcon}
            />
          </div>
          <div className="frame__content">
            <form
              className="video__form"
              id={formId}
              onSubmit={handleSubmit(submit)}
            >
              <TextArea
                labelName="Название"
                isResizble={false}
                maxLength={100}
                onChange={(e) => setVideoName(e.target.value)}
                value={videoName}
              />
              <TextArea
                labelName="Описание"
                isResizble
                maxLength={1000}
                onChange={(e) => setVideoDescription(e.target.value)}
                value={videoDescription}
              />
              <DropFileInput fileSet={setVideoFile} />
              <ImageUploader fileSet={setThumbFile}/>
            </form>
            <div className="frame__select"></div>
          </div>
          <div className="frame__foot">
            <CButton
              type="submit"
              form={formId}
              buttonType="primary"
              style={{ width: "100%" }}
            >
              Загрузить
            </CButton>
          </div>
        </div>
      </div>
    </>
  );
};

// {!!channelsQuery.data &&
// {channelsQuery.data.content.map((channel: IChannel) => (
//   <div
//     className="frame__option"
//     onClick={() => setSelectValue([channel.id, channel.name])}
//   >
//     {channel.name}
//   </div>
// ))}}
export default UploadPage;
