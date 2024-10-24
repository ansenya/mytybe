import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { IChannel, IComment, IUser, IVideo } from "../../models";
import { AuthCredentials, IToken } from "../../models/AuthModels";
import { ChannelsRequest } from "../../models/ChannelModels";
import {
  UploadResponse,
  VideosRequest,
  UploadRequest,
  PaginationResponse,
  CommentRequest,
  PostCommentRequest,
} from "../../models/VideoModels";
import { RegisterArgs } from "../../pages/authPages/registrationPage";
import {BaseQueryArg} from "@reduxjs/toolkit/dist/query/baseQueryTypes";

export const serverApi = createApi({
  reducerPath: "server",
  baseQuery: fetchBaseQuery({
    // baseUrl: process.env.API_BASE_URL || "https://video-spot.ru/api",
    baseUrl: process.env.API_BASE_URL || "http://localhost:1984/api",
  }),
  endpoints: (build) => ({
    getUsers: build.query<PaginationResponse<IUser>, void>({
      query: () => ({
        url: `users`,
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
    createVideoEntity: build.mutation({
      query: ({ title, description, channelId }: { title: string, description: string, channelId: string }) => ({
        url: 'videos/create',
        method: 'POST',
        params: {
          videoName: title,
          videoDescription: description,
          channelId: channelId,
        },
        headers: {
          Authorization: `Bearer ${localStorage.getItem('jwtoken')}`,
        },
      }),
    }),

    uploadVideo: build.mutation({
      query: (formData) => ({
        url: "http://localhost:8080/videos/upload",
        method: "POST",
        body: formData,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),

    updateUser: build.mutation<any, { id: number; userData: FormData }>({
      query: ({ id, userData }) => {
        return {
          url: `users/${id}`,
          method: "PUT",
          body: userData,
          headers: {
            Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
          },
        };
      },
    }),

    createChannel: build.mutation({
      query: (channelData) => ({
        url: "channels/create",
        method: "POST",
        body: channelData,
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
    getVideos: build.query<
      PaginationResponse<IVideo>,
      VideosRequest & { searchQuery?: string }
    >({
      query: ({ sort, page, size, searchQuery, channelId }) => {
        const token = localStorage.getItem("jwtoken");
        const params: VideosRequest & { q?: string } = {
          page,
          size,
          sort,
        };

        if (channelId) params.channelId = channelId;
        if (searchQuery !== undefined) params.q = searchQuery;
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        return {
          url: `videos${searchQuery !== undefined ? "/search" : ""}`,
          headers,
          params,
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

    getChannels: build.query<
      PaginationResponse<IChannel>,
      ChannelsRequest & { searchQuery?: string; isSubs?: boolean }
    >({
      query: ({ sort, page, size, searchQuery, isSubs }) => {
        const token = localStorage.getItem("jwtoken");
        const params: ChannelsRequest & { q?: string } = {
          page,
          size,
          sort,
        };
        if (searchQuery !== undefined) params.q = searchQuery;
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        const url = isSubs
          ? "users/followings"
          : `channels${searchQuery !== undefined ? "/search" : ""}`;
        return {
          url,
          headers,
          params,
        };
      },
    }),
  }),
});

export const {
  useLazyLoginQuery,
  useGetAuthQuery,
  useGetUsersQuery,
  useGetUserByIdQuery,
  useLazyGetUserByIdQuery,
  useLazyGetVideosQuery,
  useRegisterMutation,
  useCreateVideoEntityMutation,
  useGetVideoByIdQuery,
  useGetUserChannelsQuery,
  useLikeVideoMutation,
  useDislikeVideoMutation,
  useLazyGetCommentsQuery,
  usePostCommentMutation,
  useDeleteCommentByIdMutation,
  useLikeCommentMutation,
  useGetChannelByIdQuery,
  useFollowMutation,
  useDeleteVideoByIdMutation,
  useLazyGetChannelsQuery,
  useUpdateUserMutation,
  useCreateChannelMutation,
  useUploadVideoMutation,
} = serverApi;
