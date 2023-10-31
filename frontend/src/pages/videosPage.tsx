import React, {useEffect} from 'react';
import {useAppSelector} from "../hooks/redux";
import {useGetVideosQuery} from "../store/api/serverApi";

const VideosPage = () => {

    const {data, isLoading, isError} = useGetVideosQuery({
        page: 0,
        size: 10,
        sort: "desc"
    })


    useEffect(() => {
        if (!isLoading && data!==undefined){
            console.log(data)
        }
    }, [isLoading])


    return (
        <div>
            {localStorage.getItem("username")}
        </div>
    );
};

export default VideosPage;