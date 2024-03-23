import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useLazyLoginQuery } from "../../store/api/serverApi";
import { useActions } from "../../hooks/actions";
import CButton from "../../components/UI/CButton/CButton";
import FormField from "../../components/UI/FormField/FormField";
import { useForm } from "react-hook-form";
import { AuthCredentials } from "../../models/AuthModels";

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();

  const [login, { isLoading, data, isError }] = useLazyLoginQuery();

  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm<AuthCredentials>();

  useEffect(() => {
    if (!isLoading && data !== undefined) {
      setToken(data[1]);
      setIsLoaded(true);
      setIsError(false);
      setUser(data[0]);
      navigate(pathFrom || "/", { replace: true });
    }
    if (isError) {
      setIsError(true);
    }
  }, [isLoading]);

  return (
    <div className="login__content">
      <div className="form">
        <h1 className="form__name">Sign in to Spot</h1>
        <FormField
          {...register("username")}
          customPlaceholder={"Имя пользователя"}
          error={errors.username?.message}
          autoComplete={"username"}
        />
        <FormField
          {...register("password")}
          isPassword
          customPlaceholder={"Введите пароль"}
          error={errors.password?.message}
        />
      </div>
    </div>
  );
};

export default LoginPage;
