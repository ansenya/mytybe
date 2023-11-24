import React, { FC, useEffect } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useActions } from "../hooks/actions";
import { useGetAuthQuery } from "../store/api/serverApi";

interface RequireAuthProps {
  children?: React.ReactElement;
}

const RequireAuth: FC<RequireAuthProps> = ({ children }) => {
  const { setUser } = useActions();
  const location = useLocation();

  const { isLoading, isError, data } = useGetAuthQuery();

  useEffect(() => {
    if (data) {
      console.log(data[0].name)
      setUser(data[0]);
    }
  }, [data, setUser]);

  if (isLoading) {
    return <div>Loading...</div>;
  } else if (isError || !localStorage.getItem("jwtoken")) {
    return <Navigate to="/login" state={{ from: location }} />;
  } else {
    return <>{children}</>;
  }
};

export default RequireAuth;
