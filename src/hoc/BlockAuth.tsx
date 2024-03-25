import React, { FC, useEffect } from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useActions } from "../hooks/actions";

interface BlockAuthProps {
  children?: React.ReactElement;
}

const BlockAuth: FC<BlockAuthProps> = ({ children }) => {


  if (localStorage.getItem("jwtoken")) {
    return <Navigate to="/" />;
  } else {
    return <>{children}</>;
  }
};

export default BlockAuth;
