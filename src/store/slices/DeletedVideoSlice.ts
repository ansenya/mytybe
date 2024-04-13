import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface DeletedVideosState {
  deletedIds: number[];
}

const initialState: DeletedVideosState = {
  deletedIds: [],
};

const deletedVideosSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    addDeletedVideos(state, payload: PayloadAction<number>) {
      state.deletedIds.push(payload.payload);
    },
  },
});

export const deletedVideosActions = deletedVideosSlice.actions;
export const deletedVideosReducer = deletedVideosSlice.reducer;
