import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IComment } from "../../models";

interface CommentPostState {
  comment: IComment | null; 
  commentToResponseId: number | null;
  toDelete: boolean;
}

const initialState: CommentPostState = {
  comment: null,
  commentToResponseId: null,
  toDelete: false
};

const CommentPostSlice = createSlice({
  name: "commentPost",
  initialState,
  reducers: {
    setCommentPost(state, payload: PayloadAction<CommentPostState>) {
      state.comment = payload.payload.comment;
      state.commentToResponseId = payload.payload.commentToResponseId;
      state.toDelete = payload.payload.toDelete;
    }
  },
});

export const commentActions = CommentPostSlice.actions;
export const commentReducer = CommentPostSlice.reducer;
