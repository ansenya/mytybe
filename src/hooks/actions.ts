import {bindActionCreators} from "@reduxjs/toolkit";
import {useDispatch} from "react-redux";
import {authActions} from "../store/slices/authSlice";

const actions = {
    ...authActions
}

export const useActions = () => {
    const dispatch = useDispatch()
    return bindActionCreators(actions, dispatch)
}
