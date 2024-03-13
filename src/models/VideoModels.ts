import { IVideo } from "./index";


export interface UploadRequest {
  channelId: number;
  imageFile?: File;
  videoFile: File;
  videoName?: string;
  videoDescription?: string;
}

export interface UploadResponse {}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: Sort;
  offset: number;
  paged: boolean;
  unpaged: boolean;
}

export interface Sort {
  empty: boolean;
  sorted: boolean;
  unsorted: boolean;
}

export interface VideosRequest {
  sort: "desc" | "asc";
  page: number;
  size: number;
}

export interface PaginationResponse<T> {
  content: T[];
  pageable: Pageable;
  last: boolean;
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  sort: Sort;
  numberOfElements: number;
  first: boolean;
  empty: boolean;
}
