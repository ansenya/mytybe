import React, { useEffect, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { useLoginQuery } from "../store/api/serverApi";
import { useActions } from "../hooks/actions";
import CButton from "../components/UI/CButton/CButton";
import FormField from "../components/UI/FormField/FormField";

const LoginPage = () => {
  const location = useLocation();
  const navigate = useNavigate();
  const pathFrom = location.state?.from?.pathname;

  const { setToken, setUser, setIsError, setIsLoaded } = useActions();

  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isSubmitted, setIsSubmitted] = useState<boolean>(false);

  const { data, isLoading, isError, error } = useLoginQuery(
    {
      username,
      password,
    },
    {
      skip: !isSubmitted,
    },
  );

  useEffect(() => {
    if (!isLoading && data !== undefined) {
      setToken(data[1]);
      setIsLoaded(true);
      setIsError(false);
      setUser(data[0]);
      navigate(pathFrom || "/", { replace: true });
    }
    if (isError) {
      setIsSubmitted(false);
      setIsError(true);
    }
  }, [isLoading]);

  const onClick = (e: React.MouseEvent) => {
    setIsSubmitted(true);
  };

  return (
    <div className="login__content">
      <div className="form">
        <h1 className="form__name">Sign in to Spot</h1>
        <FormField
          isPassword={false}
          fieldName="username"
          onChange={(e) => setUsername(e.target.value)}
          value={username}
        />
        <FormField
          isPassword={true}
          fieldName="password"
          onChange={(e) => setPassword(e.target.value)}
          value={password}
        />
        <CButton
          style={{ width: "100%" }}
          buttonType="primary"
          onClick={onClick}
        >
          Sign in
        </CButton>
      </div>
    </div>
  );
};

export default LoginPage;
