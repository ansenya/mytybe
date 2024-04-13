import {bindActionCreators} from "@reduxjs/toolkit";
import {useDispatch} from "react-redux";
import {authActions} from "../store/slices/authSlice";
import { commentActions } from "../store/slices/commentPostSlice";
import { deletedVideosActions } from "../store/slices/DeletedVideoSlice";
import { focusActions } from "../store/slices/focusSlice";

const actions = {
    ...authActions,
    ...focusActions, 
    ...commentActions,
    ...deletedVideosActions,
}

export const useActions = () => {
    const dispatch = useDispatch()
    return bindActionCreators(actions, dispatch)
}
