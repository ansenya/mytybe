import React, { useId, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import "./uploadPage.scss";
import closeIcon from "../../assets/arrow-to-right-svgrepo-com.svg";
import IconButton from "../../components/UI/IconButton/IconButton";
import CButton from "../../components/UI/CButton/CButton";
import { useUploadVideoMutation } from "../../store/api/serverApi";
import { useAppSelector } from "../../hooks/redux";
import { SubmitHandler, useForm } from "react-hook-form";
import { UploadRequest } from "../../models/VideoModels";


type UploadForm = Omit<UploadRequest, "channelId">;

const UploadPage = () => {
  const formId = useId();
  const navigate = useNavigate();
  const location = useLocation();
  const [post, { data, isLoading, isError, error }] = useUploadVideoMutation();
  const { user } = useAppSelector((state) => state.auth);

  const {register, handleSubmit, formState: {errors, }} = useForm<UploadForm>();

  const [selectValue, setSelectValue] = useState<[number, string]>([
    user?.channels[0].id,
    user?.channels[0].name,
  ]);

  const submit: SubmitHandler<UploadForm> = (data) => {
    post({...data, selectValue[0]});
  }

  useEffect(() => {
    if (data) {
      navigate(location.state?.from || "/")
    }
  }, [data])

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
            <form className="video__form" id={formId} onSubmit={handleSubmit(submit)}>
              <input type="textarea" {...register("videoName", {
                maxLength: {
                  value: 100,
                  message: "Максимальная длина 100"
                }
              })}/>
              <input type="textarea" {...register("videoDescription")}/>
              <input type="file" {...register("videoFile"), {
                required: true,
              }}/>
              <input type="file" {...register("imageFile")}/>
            </form>
            <div className="frame__select">
              {user?.channels.map((channel) => (
                <div
                  className="frame__option"
                  onClick={() => setSelectValue([channel.id, channel.name])}
                >
                  {channel.name}
                </div>
              ))}
            </div>
          </div>
          <div className="frame__foot">
            <CButton type="submit" form={formId} buttonType="primary">Загрузить</CButton>
          </div>
        </div>
      </div>
    </>
  );
};

export default UploadPage;
