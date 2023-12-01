import { createApi, fetchBaseQuery } from "@reduxjs/toolkit/query/react";
import { IUser } from "../../models";
import { AuthCredentials, IToken } from "../../models/AuthModels";
import { VideosRequest, VideosResponse } from "../../models/VideoModels";

export const serverApi = createApi({
  reducerPath: "server",
  baseQuery: fetchBaseQuery({
    baseUrl: "http://5.180.174.71:1984/api/",
  }),
  endpoints: (build) => ({
    getUsers: build.query({
      query: () => ({
        url: `u/all`,
        params: {
          page: 0,
          size: 10,
        },
      }),
    }),
    getUserById: build.query<IUser, number>({
      query: (id: number) => ({
        url: `u/user`,
        params: {
          id: id,
        },
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`,
        },
      }),
    }),
    login: build.query<[IUser, IToken], AuthCredentials>({
      query: ({ username, password }) => ({
        url: `u/auth/login`,
        headers: {
          Authorization: `Basic ${btoa(`${username}:${password}`)}`,
        },
      }),
    }),
    getAuth: build.query<[IUser, IToken], void>({
      query: () => ({
        url: `u/auth/login`,
        headers: {
          Authorization: `Bearer ${localStorage.getItem("jwtoken")}`
        },
      }),
    }),
    getVideos: build.query<VideosResponse, VideosRequest>({
      query: ({ sort, page, size }) => ({
        url: `v/all`,
        params: {
          page,
          sort,
          size,
        },
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
} = serverApi;
