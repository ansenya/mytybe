import { createSlice, PayloadAction } from "@reduxjs/toolkit";

import { IUser } from "../../models";
import { IToken } from "../../models/AuthModels";

interface AuthState {
  isLoaded: boolean;
  isError: boolean;
  user: IUser | null;
}

const initialState: AuthState = {
  user: null,
  isError: false,
  isLoaded: false,
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setIsError(state, payload: PayloadAction<boolean>) {
      state.isError = payload.payload;
    },
    setIsLoaded(state, payload: PayloadAction<boolean>) {
      state.isLoaded = payload.payload;
    },
    setUser(state, payload: PayloadAction<IUser>) {
      state.user = payload.payload;
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
