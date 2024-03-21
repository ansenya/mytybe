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
  duration: any;
  views: number;
  qualities: string[];
  path: string;
  thumbnail: string;
  likedByThisUser: boolean;
  dislikedByThisUser: boolean;
  channel: IChannel;
  explicit: boolean;
  processedQualities: boolean;
  processedAi: boolean;
  streamStatus: number;
  created: string;
  updated: string;
  dislikes: number;
  likes: number;
}

export interface IComment {
  id: number
  text: string
  deleted: boolean
  created: string
  updated: string
  nextComments: IComment[]
  user: IUser
  channel: IChannel
  likesAmount: number,
  likedByThisUser?: boolean,
}
