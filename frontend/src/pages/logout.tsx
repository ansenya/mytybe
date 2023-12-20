import React, { useEffect } from "react";
import { Navigate, useNavigate } from "react-router-dom";
import { useActions } from "../hooks/actions";

const Logout = () => {
  const {clearAuth} = useActions();
  const navigate = useNavigate();

  useEffect(() => {
    clearAuth();
    // navigate("/", {replace: true})
  }, [])

  return <Navigate to='/' replace={true}/>
}

export default Logout;
