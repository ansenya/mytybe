import React, {useEffect, useState} from 'react';
import {useAppSelector} from "../hooks/redux";
import {useGetVideosQuery} from "../store/api/serverApi";
import {IVideo} from "../models";
import VideoLine from "../components/VideoLine"

const VideosPage = () => {

    const {data, isLoading, isError} = useGetVideosQuery({
        page: 0,
        size: 10,
        sort: "desc"
    })

    const [videos, setVideos] = useState<IVideo[]>([])

    useEffect(() => {
        if (!isLoading && data!==undefined){
            setVideos(data.content)
        }
    }, [isLoading])


    return (
        <div>
            <VideoLine videos={videos} categoryName="fuck"/>
            <span style={{color: 'transparent'}}>penis</span>
        </div>
    );
};

export default VideosPage;