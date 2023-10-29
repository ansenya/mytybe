export interface IUser {
    id?: number;
    username?: string;
    name?: string;
    surname?: string;
    role?: string;
    sex?: string;
    pfp?: any;
    age?: number;
    country?: any;
    channels?: Channel[];
    lastViewed?: any[];
    deleted?: boolean;
    locked?: boolean;
    created?: string;
    updated?: string;
}

export interface Channel {
    id: number;
    name: string;
    videosAmount: number;
    followersAmount: number;
    deleted: boolean;
    pfp?: any;
}


export interface IToken {
    token: string
}


export interface AuthCredentials {
    username: string,
    password: string
}