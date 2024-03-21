import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { IComment } from "../../models";

interface CommentPostState {
  comment: IComment | null; 
  commentToResponseId: number | null;
}

const initialState: CommentPostState = {
  comment: null,
  commentToResponseId: null,
};

const CommentPostSlice = createSlice({
  name: "commentPost",
  initialState,
  reducers: {
    setCommentPost(state, payload: PayloadAction<CommentPostState>) {
      state.comment = payload.payload.comment;
      state.commentToResponseId = payload.payload.commentToResponseId;
    }
  },
});

export const commentActions = CommentPostSlice.actions;
export const commentReducer = CommentPostSlice.reducer;
