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
import ChannelsSelect from "../../components/UI/ChannelsSelect/ChannelsSelect";
import ErrorMessage from "../../components/UI/ErrorMessage/ErrorMessage";
import { fileValidationError, lengthValidationError } from "./validators";

type UploadForm = Omit<UploadRequest, "channelId">;

const UploadPage = () => {
  const formId = useId();
  const navigate = useNavigate();
  const location = useLocation();
  const [post, { data, isLoading, isError, error }] = useUploadVideoMutation();
  const { user, isLoaded } = useAppSelector((state) => state.auth);

  const [videoFile, setVideoFile] = useState<File | null>(null);
  const [channelId, setChannelId] = useState<number>(0);
  const [imageFile, setImageFile] = useState<File | null>(null);
  const [videoDescription, setVideoDescription] = useState<string>("");
  const [videoName, setVideoName] = useState<string>("");

  const [errors, setErrors] = useState({
    nameError: "",
    descError: "",
    videoFileError: "",
  });

  useEffect(() => {
    if (data) {
      console.log(data);
    }
  }, [data]);

  const handleUpload = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const nameError = lengthValidationError(videoName, 100);
    const descError = lengthValidationError(videoDescription, 1000);
    const videoError = fileValidationError(videoFile, "video");
    setErrors((prevstate) => {
      return {
        descError: descError,
        nameError: nameError,
        videoFileError: videoError,
      };
    });

    if (descError || nameError || videoError || videoFile === null || channelId === 0) return;

    const postFormData = new FormData();
    postFormData.append("channelId", String(channelId));
    postFormData.append("videoFile", videoFile)

    if (videoName) postFormData.append("videoName", videoName);
    if (videoDescription) postFormData.append("videoDescription", videoDescription);
    if (imageFile) postFormData.append("imageFile", imageFile);

    post(postFormData);
  };

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
              onSubmit={handleUpload}
            >
              <div className="field__container">
                <TextArea
                  labelName="Название"
                  isResizble={false}
                  maxLength={100}
                  onChange={(e) => setVideoName(e.target.value)}
                  value={videoName}
                />
                {!!errors.nameError && <ErrorMessage msg={errors.nameError} />}
              </div>

              <div className="field__container">
                <TextArea
                  labelName="Описание"
                  isResizble
                  maxLength={1000}
                  onChange={(e) => setVideoDescription(e.target.value)}
                  value={videoDescription}
                />
                {!!errors.descError && <ErrorMessage msg={errors.descError} />}
              </div>

              <div className="field__container">
                <DropFileInput fileSet={setVideoFile} />
                {!!errors.videoFileError && (
                  <ErrorMessage msg={errors.videoFileError} />
                )}
              </div>
              <h1 style={{ fontSize: "1.2rem" }}>
                Добавить изображение и выбрать канал
              </h1>
              <div className="bottom">
                <ImageUploader fileSet={setImageFile} />
                {isLoaded && (
                  <ChannelsSelect
                    id={user?.id || 0}
                    setChannelId={setChannelId}
                  />
                )}
              </div>
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

export default UploadPage;
