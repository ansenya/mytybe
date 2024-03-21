import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface FocusState {
  isFocused: boolean;
  focusTargetId: string | null; 
}

const initialState: FocusState = {
  isFocused: false,
  focusTargetId: null,
};

const focusSlice = createSlice({
  name: "focus",
  initialState,
  reducers: {
    setIsFocused(state, payload: PayloadAction<FocusState>) {
      state.isFocused = payload.payload.isFocused 
      state.focusTargetId = payload.payload.focusTargetId
    },
  },
});

export const focusActions = focusSlice.actions;
export const focusReducer = focusSlice.reducer;
