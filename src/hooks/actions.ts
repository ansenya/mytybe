import {bindActionCreators} from "@reduxjs/toolkit";
import {useDispatch} from "react-redux";
import {authActions} from "../store/slices/authSlice";
import { focusActions } from "../store/slices/focusSlice";

const actions = {
    ...authActions,
    ...focusActions
}

export const useActions = () => {
    const dispatch = useDispatch()
    return bindActionCreators(actions, dispatch)
}
