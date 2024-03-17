import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface FocusState {
  isFocused: boolean;
}

const initialState: FocusState = {
  isFocused: false
};

const focusSlice = createSlice({
  name: "focus",
  initialState,
  reducers: {
    setIsFocused(state, payload: PayloadAction<boolean>) {
      state.isFocused = payload.payload 
    },
  },
});

export const focusActions = focusSlice.actions;
export const focusReducer = focusSlice.reducer;
