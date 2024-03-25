import React, { useEffect, useState, useMemo } from "react";
import {
  useForm,
  SubmitHandler,
  useFieldArray,
  RegisterOptions,
} from "react-hook-form";
import {
  useGetUsersQuery,
  useRegisterMutation,
} from "../../store/api/serverApi";
import FormField from "../../components/UI/FormField/FormField";
import CButton from "../../components/UI/CButton/CButton";
import { useLocation, useNavigate } from "react-router-dom";
import { useActions } from "../../hooks/actions";
import styles from "./authPages.module.scss";
import InlineLoader from "../../components/UI/Loader/InlineLoader";
import useDebounce from "../../hooks/useDebounce";
import { Link } from "react-router-dom";

export interface RegisterArgs {
  username: string;
  password: string;
  surname?: string;
  name: string;
}

const RegistrationPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();
  const [post, { data, isLoading, isError, error }] = useRegisterMutation();

  const usersQuery = useGetUsersQuery();

  const {
    register,
    handleSubmit,
    getValues,
    formState: { errors },
    trigger,
  } = useForm<RegisterArgs & { repeatPassword: string }>({
    mode: "onSubmit",
  });

  const submit: SubmitHandler<RegisterArgs> = (data) => {
    post(data);
  };

  useEffect(() => {
    if (!isLoading && data) {
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
    <div className={styles.login}>
      <form onSubmit={handleSubmit(submit)}>
        <h1 className={styles.formName}>Регистрация</h1>
        <FormField
          register={register}
          name="username"
          options={
            {
              onChange: () => trigger("username"),
              required: "Поле обязательно к заполнению",
              maxLength: {
                value: 100,
                message: "Имя пользователя должно быть не длинее 100 символов",
              },
              validate: (fieldValue) => {
                if (usersQuery.data) {
                  return (
                    !usersQuery.data?.content
                      .map((user) => user.username)
                      .includes(fieldValue) ||
                    "Пользователь с таким именем уже существует"
                  );
                }
              },
            } as RegisterOptions
          }
          customPlaceholder={"Имя пользователя"}
          error={errors.username?.message}
          autoComplete={"username"}
        />
        <FormField
          register={register}
          name="password"
          options={
            {
              required: "Поле обязательно к заполнению",
              maxLength: {
                value: 100,
                message: "Пароль должен быть не длинее 100 символов",
              },
              minLength: {
                value: 7,
                message: "Пароль должен быть не короче 7 символов",
              },
            } as RegisterOptions
          }
          isPassword
          customPlaceholder={"Введите пароль"}
          error={errors.password?.message}
        />
        <FormField
          register={register}
          name="repeatPassword"
          options={{
            onBlur: () => trigger("repeatPassword"),
            validate: (value: string) =>
              getValues().password === value || "Пароли не совпадают",
          }}
          isPassword
          customPlaceholder={"Повторите"}
          error={errors.repeatPassword?.message}
        />

        <FormField
          register={register}
          name="name"
          options={
            {
              required: "Поле обязательно к заполнению",
              maxLength: {
                value: 100,
                message: "Имя должно быть не длинее 100 символов",
              },
            } as RegisterOptions
          }
          customPlaceholder={"Имя"}
          error={errors.name?.message}
          autoComplete={"given-name"}
        />
        <FormField
          register={register}
          name="surname"
          options={
            {
              maxLength: {
                value: 100,
                message: "Фамилия должна быть не длинее 100 символов",
              },
            } as RegisterOptions
          }
          customPlaceholder={"Фамилия (необязательно)"}
          error={errors.surname?.message}
          autoComplete={"family-name"}
        />
        {isError && <h3 className={styles.formError}>Ошибка регистрации</h3>}
        {isLoading ? (
          <InlineLoader />
        ) : (
          <button type="submit">Зарегистрироваться</button>
        )}
        <div className={styles.bottomLink}>
          <span>Есть аккаунт?</span>
          <Link className={styles.link} to="/login">
            Войдите
          </Link>
        </div>
      </form>
    </div>
  );
};

export default RegistrationPage;
