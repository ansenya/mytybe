import {createSlice, PayloadAction} from "@reduxjs/toolkit";

import {IUser, IToken} from "../../models";


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
        },
        clearAuth(state){
            state = initialState
            localStorage.removeItem('jwtoken')
        }
    }

})

export const authActions = authSlice.actions
export const authReducer = authSlice.reducer