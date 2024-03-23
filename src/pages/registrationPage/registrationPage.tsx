import React, { useEffect, useState, useMemo } from "react";
import { useForm, SubmitHandler, useFieldArray } from "react-hook-form";
import { useRegisterMutation } from "../../store/api/serverApi";
import FormField from "../../components/UI/FormField/FormField";
import CButton from "../../components/UI/CButton/CButton";
import { useLocation, useNavigate } from "react-router-dom";
import { useActions } from "../../hooks/actions";

export interface RegisterArgs {
  username: string;
  password: string;
  surname: string;
  name: string;
}

const RegistrationPage = () => {
  const navigate = useNavigate()
  const [post, { data, isLoading, isError, error}] = useRegisterMutation();
  const { register, handleSubmit, formState: {errors} } = useForm<RegisterArgs>();


  const submit: SubmitHandler<RegisterArgs> = (data) => {
    post(data);
  };


  useEffect(() => {
    if (data){
      navigate("/login", {replace: true})
    } 
  }, [data])
  return (
    <div className="login__content">
      <form onSubmit={handleSubmit(submit)} className="form">
        <h1 className="form__name">Create an account</h1>

        <FormField
          customPlaceholder="fuck"
          isPassword={false}
          {...register("username", {
            required: true,
          })}
        />

        <FormField
          customPlaceholder="fuck"
          isPassword={true}

          {...register("password", {
            required: true,
            validate: (value: string) => value.length >= 7
          })}
        />

        <FormField

          customPlaceholder="fuck"
          isPassword={false}
          {...register("name")}
        />

        <FormField
          customPlaceholder="fuck"
          isPassword={false}
          {...register("surname")}
        />

        <CButton style={{ width: "100%" }} buttonType="primary" type="submit" disabled={isLoading}>
          Sign up
        </CButton>
      </form>
    </div>
  );
};

export default RegistrationPage;
