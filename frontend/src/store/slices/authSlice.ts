import { createSlice, PayloadAction } from "@reduxjs/toolkit";

import { IUser } from "../../models";
import { IToken } from "../../models/AuthModels";

interface AuthState {
  user: IUser | null;
}

const initialState: AuthState = {
  user: null,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setUser(state, payload: PayloadAction<IUser>){
      state.user = payload.payload
    },
    setToken(state, payload: PayloadAction<IToken>) {
      localStorage.setItem("jwtoken", payload.payload.token);
    },
    clearAuth(state) {
      state = initialState;
      localStorage.removeItem("jwtoken");
    },
  },
});

export const authActions = authSlice.actions;
export const authReducer = authSlice.reducer;
