import React, {useEffect, useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {useLoginQuery} from "../store/api/serverApi";
import {ClickableInputOrButton} from "@testing-library/user-event/utils/click/isClickableInput";
import {useActions} from "../hooks/actions";

const LoginPage = () => {

    const location = useLocation();
    const navigate = useNavigate();
    const pathFrom = location.state?.from?.pathname

    const {setAuth} = useActions()

    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const [isSubmitted, setIsSubmitted] = useState<boolean>(false)

    const {data, isLoading, isError, error} = useLoginQuery(
        {
            username,
            password
        },
        {
            skip: !isSubmitted,
        }
    )

    useEffect(() => {
        if (!isLoading && data !== undefined ){
            setAuth(data)
            navigate(pathFrom || '/', {replace: true})
        }
        if (isError){
            setIsSubmitted(false)
            setUsername('')
            setPassword('')
        }
    }, [isLoading])

    const onClick = (e: React.MouseEvent<ClickableInputOrButton>) => {
        setIsSubmitted(true)
    }

    return (
        <div>
            <input
                type="text"
                value={username}
                onChange={(event) => {setUsername(event.target.value)}}
            />
            <input
                type="password"
                value={password}
                onChange={(event) => {setPassword(event.target.value)}}
            />
            <button onClick={onClick}>log in</button>
        </div>
    );
};

export default LoginPage;