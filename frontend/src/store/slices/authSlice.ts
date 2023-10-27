import {createSlice, PayloadAction} from "@reduxjs/toolkit";
import {AuthResponse, IUser} from "../../models";

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
        setAuth(state, payload: PayloadAction<AuthResponse>){
            state.user = payload.payload.user
            localStorage.setItem('jwtoken', payload.payload.token)
        },
        clearAuth(state, payload: PayloadAction){
            state = initialState
            localStorage.removeItem('jwtoken')
        }
    }

})

export const authActions = authSlice.actions
export const authReducer = authSlice.reducer