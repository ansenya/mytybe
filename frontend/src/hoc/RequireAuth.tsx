import React, { FC, useEffect } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useActions } from "../hooks/actions";

interface RequireAuthProps {
  children?: React.ReactElement;
}

const RequireAuth: FC<RequireAuthProps> = ({ children }) => {
  const location = useLocation();


  if (!localStorage.getItem("jwtoken")) {
    return <Navigate to="/login" state={{ from: location }} />;
  } else {
    return <>{children}</>;
  }
};

export default RequireAuth;
