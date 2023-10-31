import React, {FC} from 'react';
import {Navigate, useLocation} from "react-router-dom";

interface RequireAuthProps {
    children?: React.ReactElement
}


export const RequireAuth = ({children}: RequireAuthProps) => {
    let auth = false;
    const location = useLocation()


    if (!localStorage.getItem("jwtoken")){
        return <Navigate to="/login" state={{from: location}}/>
    }

    return <>{children}</>
};

export default RequireAuth;