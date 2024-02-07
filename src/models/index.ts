export interface IUser {
    id: number;
    username: string;
    name: string;
    surname: string;
    role: string;
    sex: string;
    pfp: string;
    age: number;
    country: any;
    channels: IChannel[];
    lastViewed: any[];
    deleted: boolean;
    locked: boolean;
    created: string;
    updated: string;
}

export interface IChannel {
    id: number;
    name: string;
    videosAmount: number;
    followersAmount: number;
    deleted: boolean;
    chp: string;
}

export interface IVideo {
    id: number;
    name: string;
    description: string;
    duration?: any;
    views: number;
    thumbnail: string;
    channel: IChannel;
    category?: string;
    path: string;
    tags: string[];
    processed: boolean;
    explicit: boolean;
    deleted: boolean;
    stream: boolean;
    created: string;
    updated: string;
}





