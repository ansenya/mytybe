import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { IChannel, IComment, IUser, IVideo } from "../../models";
import { AuthCredentials, IToken } from "../../models/AuthModels";
import {
  UploadResponse,
  VideosRequest,
  UploadRequest,
  PaginationResponse,
  CommentRequest,
  PostCommentRequest,
} from "../../models/VideoModels";
import { RegisterArgs } from "../../pages/registrationPage";

export const serverApi = createApi({
  reducerPath: "server",
  baseQuery: fetchBaseQuery({
    baseUrl: "http://video-spot.ru/api",
  }),
  endpoints: (build) => ({
    getUsers: build.query({
      query: () => ({
        url: `users`,
        params: {
          page: 0,
          size: 10,
        },
      }),
    }),
    getUserById: build.query<IUser, number>({
      query: (id: number) => ({
        url: `users/${id}`,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    login: build.query<[IUser, IToken], AuthCredentials>({
      query: ({ username, password }) => ({
        url: `auth/login`,
        headers: {
          Authorization: `Basic ${btoa(`${username}:${password}`)}`,
        },
      }),
    }),
    register: build.mutation<[IUser, IToken], RegisterArgs>({
      query: (registerData) => ({
        url: "auth/register",
        method: "POST",
        body: { ...registerData },
      }),
    }),
    uploadVideo: build.mutation({
      query: (videoData) => ({
        url: "videos/upload",
        method: "POST",
        body: videoData,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),

    likeVideo: build.mutation({
      query: (id: number) => ({
        url: `videos/like/${id}`,
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    dislikeVideo: build.mutation({
      query: (id: number) => ({
        url: `videos/dislike/${id}`,
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    getAuth: build.query<[IUser, IToken], void>({
      query: () => ({
        url: `auth/login`,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    getUserChannels: build.query<PaginationResponse<IChannel>, number>({
      query: (uid: number) => ({
        url: `channels`,
        params: {
          page: 0,
          size: 10,
          uid: uid,
        },
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    getSearchedVideos: build.query<
      PaginationResponse<IVideo>,
      VideosRequest & { searchQuery: string }
    >({
      query: ({ sort, page, size, searchQuery }) => {
        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        return {
          url: `search`,
          params: {
            page,
            size,
            sort,
            q: searchQuery,
          },
          headers,
        };
      },
    }),
    getVideos: build.query<PaginationResponse<IVideo>, VideosRequest>({
      query: ({ sort, page, size, channelId }) => {
        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        const params: VideosRequest = {
          page,
          sort,
          size,
        };

        if (channelId) params.channelId = channelId;

        return {
          url: `videos`,
          params,
          headers,
        };
      },
    }),
    getVideoById: build.query<IVideo, number>({
      query: (id) => {
        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        return {
          url: `videos/${id}`,
          headers,
        };
      },
    }),
    getComments: build.query<PaginationResponse<IComment>, CommentRequest>({
      query: ({ sort, page, size, videoId, commentId }) => {
        const params: CommentRequest = {
          page,
          sort,
          size,
          videoId,
        };

        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        if (commentId) params.commentId = commentId;
        return {
          url: `comments`,
          params,
          headers,
        };
      },
    }),
    postComment: build.mutation<IComment, PostCommentRequest>({
      query: ({ videoId, text, commentId }) => {
        const params: PostCommentRequest = {
          videoId,
          text,
        };
        if (commentId) params.commentId = commentId;
        return {
          url: "comments/create",
          method: "POST",
          params,
          headers: {
            Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
          },
        };
      },
    }),
    deleteCommentById: build.mutation({
      query: (id: number) => ({
        url: `comments/${id}`,
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    likeComment: build.mutation({
      query: (id: number) => ({
        url: `comments/like/${id}`,
        method: "POST",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    follow: build.mutation({
      query: (channelId: number) => ({
        url: `channels/follow`,
        method: "POST",
        params: {
          channelId,
        },
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    getChannelById: build.query<IChannel, number>({
      query: (id) => {
        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        return {
          url: `channels/${id}`,
          headers,
        };
      },
    }),

    deleteVideoById: build.mutation({
      query: (id: number) => ({
        url: `videos/${id}`,
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
  }),
});

export const {
  useLoginQuery,
  useGetAuthQuery,
  useGetUsersQuery,
  useGetUserByIdQuery,
  useLazyGetUserByIdQuery,
  useGetVideosQuery,
  useLazyGetVideosQuery,
  useRegisterMutation,
  useUploadVideoMutation,
  useGetVideoByIdQuery,
  useGetUserChannelsQuery,
  useLazyGetSearchedVideosQuery,
  useLikeVideoMutation,
  useDislikeVideoMutation,
  useLazyGetCommentsQuery,
  usePostCommentMutation,
  useDeleteCommentByIdMutation,
  useLikeCommentMutation,
  useGetChannelByIdQuery,
  useFollowMutation,
  useDeleteVideoByIdMutation,
} = serverApi;
