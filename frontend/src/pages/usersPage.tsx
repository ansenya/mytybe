import React from 'react';
import {useGetUserByIdQuery, useGetUsersQuery} from "../store/api/serverApi";

const UsersPage = () => {
    const {data, isError} = useGetUserByIdQuery(1)

    return (
        <div>
            {isError || data}
        </div>
    );
};

export default UsersPage;