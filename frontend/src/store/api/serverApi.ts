import {createApi, fetchBaseQuery} from "@reduxjs/toolkit/query/react";
import {AuthCredentials, IToken, IUser} from '../../models'


export const serverApi = createApi({
    reducerPath: 'server',
    baseQuery: fetchBaseQuery({
        baseUrl: "http://5.180.174.71:1984/api/",
    }),
    endpoints: build => ({
        getUsers: build.query({
            query: () => ({
                url: `u/all`,
                params: {
                    page: 0,
                    size: 10
                }
            })
        }),
        getUserById: build.query<any, number>({
            query: (id: number) => ({
                url: `u/user`,
                params: {
                    id: id
                }
            })
        }),
        login: build.query<[IUser, IToken], AuthCredentials>({
            query: ({username, password}) => ({
                url: `u/auth/login`,
                headers: {
                    Authorization: `Basic ${btoa(`${username}:${password}`)}`
                }
            }),
        })
    })
})

export const {
    useLoginQuery,
    useGetUsersQuery,
    useGetUserByIdQuery
} = serverApi



