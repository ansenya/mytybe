import React, {useState} from 'react';
import {useLocation, useNavigate} from "react-router-dom";
import {useAppSelector} from "../hooks/redux";
import {useLoginQuery} from "../store/api/serverApi";
import {ClickableInputOrButton} from "@testing-library/user-event/utils/click/isClickableInput";

const LoginPage = () => {

    const location = useLocation();
    const navigate = useNavigate();
    const pathFrom = location.state?.from?.pathname

    const [username, setUsername] = useState<string>('')
    const [password, setPassword] = useState<string>('')
    const [isSubmitted, setIsSubmitted] = useState<boolean>(false)

    const {data, isLoading, isError, error} = useLoginQuery(
        {
            username,
            password
        },
        {
            skip: !isSubmitted
        }
    )

    const onClick = (e: React.MouseEvent<ClickableInputOrButton>) => {
        setIsSubmitted(true)
        if (isError){
            console.log(error)
            return
        }
        console.log(data?.token)
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