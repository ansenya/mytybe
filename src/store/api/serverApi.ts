import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { IChannel, IUser, IVideo } from "../../models";
import { AuthCredentials, IToken } from "../../models/AuthModels";
import {
  UploadResponse,
  VideosRequest,
  UploadRequest,
  PaginationResponse,
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
    uploadVideo: build.mutation<UploadResponse, UploadRequest>({
      query: (videoData) => ({
        url: "videos/upload",
        method: "POST",
        body: { ...videoData },
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
    getUserChannels: build.query<PaginationResponse<IChannel[]>, number>({
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
    getVideos: build.query<PaginationResponse<IVideo>, VideosRequest>({
      query: ({ sort, page, size }) => {
        const token = localStorage.getItem("jwtoken");
        const headers = token ? { Authorization: `Bearer ${token}` } : {};
        return {
          url: `videos`,
          params: {
            page,
            sort,
            size,
            channelId: 1,
          },
          headers,
        };
      },
    }),
    getVideoById: build.query<IVideo, number>({
      query: (id) => ({
        url: `videos/${id}`,
        // headers: {
        //   Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        // },
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
} = serverApi;
