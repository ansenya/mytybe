import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useLazyLoginQuery } from "../../store/api/serverApi";
import { useActions } from "../../hooks/actions";
import CButton from "../../components/UI/CButton/CButton";
import FormField from "../../components/UI/FormField/FormField";
import { SubmitHandler, useForm } from "react-hook-form";
import { AuthCredentials } from "../../models/AuthModels";
import styles from "./loginPage.module.scss";
import InlineLoader from "../../components/UI/Loader/InlineLoader";

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();

  const [login, { isFetching, data, isError, error }] = useLazyLoginQuery();

  const {
    register,
    formState: { errors },
    handleSubmit,
  } = useForm<AuthCredentials>();

  useEffect(() => {
    if (!isFetching && data !== undefined) {
      setToken(data[1]);
      setIsLoaded(true);
      setIsError(false);
      setUser(data[0]);
      navigate(pathFrom || "/", { replace: true });
    }
    if (isError) {
      setIsError(true);
    }
  }, [isFetching]);

  const submit: SubmitHandler<AuthCredentials> = (data) => {
    login(data);
  };

  return (
    <div className={styles.login}>
      <form onSubmit={handleSubmit(submit)}>
        <h1 className={styles.formName}>Вход</h1>
        <FormField
          register={register}
          name="username"
          options={{
            required: "Поле обязательно к заполнению",
          }}
          customPlaceholder={"Имя пользователя"}
          error={errors.username?.message}
          autoComplete={"username"}
        />
        <FormField
          register={register}
          name="password"
          options={{
            required: "Поле обязательно к заполнению",
          }}
          isPassword
          customPlaceholder={"Введите пароль"}
          error={errors.password?.message}
        />

        {isError && (
          <h3 className={styles.formError}>
            Неправильный логин или пароль
          </h3>
        )}
        {isFetching ? <InlineLoader /> : <button type="submit">Войти</button>}
      </form>
      <div></div>
    </div>
  );
};

export default LoginPage;
