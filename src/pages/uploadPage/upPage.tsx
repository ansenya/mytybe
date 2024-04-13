import React, { useId, useState, useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./uploadPage.scss";
import closeIcon from "../../assets/arrow-to-right-svgrepo-com.svg";
import IconButton from "../../components/UI/IconButton/IconButton";
import CButton from "../../components/UI/CButton/CButton";
import {
  useCreateVideoEntityMutation,
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
import Popup from "../../components/UI/Popups/Popup";
import styles from "./uploadPage.module.scss";
import { toast } from "sonner";

type UploadForm = Omit<UploadRequest, "channelId">;

const UploadPage = () => {
  const formId = useId();
  const navigate = useNavigate();
  const location = useLocation();
  const [createVideo, { data, isLoading, isError, error }] =
    useCreateVideoEntityMutation();
  const [uploadVideo, uploadVideoQuery] = useUploadVideoMutation();
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

  const handleUpload = () => {
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

    if (
      descError ||
      nameError ||
      videoError ||
      videoFile === null ||
      channelId === 0
    )
      return;

    const postFormData = new FormData();
    postFormData.append("channelId", String(channelId));

    if (videoName) postFormData.append("videoName", videoName);
    if (videoDescription)
      postFormData.append("videoDescription", videoDescription);
    if (imageFile) postFormData.append("imageFile", imageFile);

    createVideo(postFormData);
  };

  const [isOpened, setIsOpened] = useState(true);
  const [step, setStep] = useState(1);

  useEffect(() => {
    if (!isOpened) {
      navigate(location.state?.from || "/");
    }
  }, [isOpened]);

  useEffect(() => {
    if (videoFile) {
      let timer = setTimeout(() => {
        setStep(2);
      }, 2000);
    }
  }, [videoFile]);

  useEffect(() => {
    if (!isLoading && data) {
      const postFormData = new FormData();
      if (videoFile) postFormData.append("file", videoFile);
      postFormData.append("uuid", data[1].uuid);
      toast.success("Видео загржено, станет доступно после обработки");
      setIsOpened(false);
    }
  }, [isLoading]);

  return (
    <>
      <Popup active={isOpened} setActive={setIsOpened}>
        <div className={styles.content}>
          <div className={styles.head}>
            <h1>Загрузить видео</h1>
            <IconButton
              onClick={() => navigate(location.state?.from || "/")}
              icon={closeIcon}
            />
          </div>
          <div className={styles.main}>
            {step === 1 ? (
              <DropFileInput fileSet={setVideoFile} />
            ) : (
              <>
                <div>
                  <TextArea
                    labelName="Название"
                    isResizble={false}
                    maxLength={100}
                    onChange={(e) => setVideoName(e.target.value)}
                    value={videoName}
                  />
                  {!!errors.nameError && (
                    <ErrorMessage msg={errors.nameError} />
                  )}
                </div>

                <div>
                  <TextArea
                    labelName="Описание"
                    isResizble
                    maxLength={1000}
                    onChange={(e) => setVideoDescription(e.target.value)}
                    value={videoDescription}
                  />
                  {!!errors.descError && (
                    <ErrorMessage msg={errors.descError} />
                  )}
                </div>

                <h1 style={{ fontSize: "1.2rem" }}>
                  Добавить изображение и выбрать канал
                </h1>
                <div className={styles.bottom}>
                  <ImageUploader fileSet={setImageFile} />
                  {isLoaded && (
                    <ChannelsSelect
                      id={user?.id || 0}
                      setChannelId={setChannelId}
                    />
                  )}
                </div>
              </>
            )}
          </div>

          <div className={styles.foot}>
            {step === 2 && (
              <CButton
                onClick={() => handleUpload()}
                form={formId}
                buttonType="primary"
                style={{ width: "100%" }}
              >
                Загрузить
              </CButton>
            )}
          </div>
        </div>
      </Popup>
    </>
  );
};

export default UploadPage;
