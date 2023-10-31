import {createSlice, PayloadAction} from "@reduxjs/toolkit";

import {IUser} from "../../models";
import {IToken} from "../../models/AuthModels";



interface AuthState {
    user: IUser | null,
}

const initialState: AuthState = {
    user: null,
}

const authSlice = createSlice({
    name: "auth",
    initialState,
    reducers: {
        setAuth(state, payload: PayloadAction<[IUser, IToken]>){
            state.user = payload.payload[0]
            localStorage.setItem('jwtoken', payload.payload[1].token)
            localStorage.setItem('id', payload.payload[0].id.toString())
            localStorage.setItem("username", payload.payload[0].username)
        },
        clearAuth(state){
            state = initialState
            localStorage.removeItem('jwtoken')
            localStorage.removeItem('id')
            localStorage.removeItem("username")
        },

    }

})

export const authActions = authSlice.actions
export const authReducer = authSlice.reducer