import { IChannel } from ".";

export interface CreateChannelRequest {}

export interface ChannelsRequest {
  sort: "desc" | "asc";
  page: number;
  size: number;
}

export interface CreateChannel {
  channel: IChannel;
}
