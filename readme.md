# Spring Service Endpoints

**Prefix**: `/api`  
**Port**: `6666`

## Authentication

- **Endpoint:** `/u/auth/login`
    - **Method:** POST
    - **Description:** Аутентификация пользователя.

- **Endpoint:** `/u/auth/register`
    - **Method:** POST
    - **Description:** Регистрация нового пользователя.

## User Endpoints

- **Endpoint:** `/u/all`
    - **Method:** GET
    - **Description:** Получить информацию о всех пользователях.

- **Endpoint:** `/u/user`
    - **Method:** GET
    - **Description:** Получить информацию о текущем пользователе.

## Video Endpoints

- **Endpoint:** `/v/all`
    - **Method:** GET
    - **Description:** Получить список всех видеозаписей.

- **Endpoint:** `/v/c/all`
    - **Method:** GET
    - **Description:** Получить список видеозаписей по каналам.

- **Endpoint:** `/v/video`
    - **Method:** GET
    - **Description:** Получить информацию о видеозаписи.

- **Endpoint:** `/v/upload`
    - **Method:** POST
    - **Description:** Загрузить новое видео.

- **Endpoint:** `/v/tag`
    - **Method:** POST
    - **Description:** Служебный ендпоинт.

## Channel Endpoints

- **Endpoint:** `/c/all`
    - **Method:** GET
    - **Description:** Получить список всех каналов.

- **Endpoint:** `/c/channel`
    - **Method:** GET
    - **Description:** Получить информацию о канале.

- **Endpoint:** `/c/create`
    - **Method:** POST
    - **Description:** Создать новый канал.

- **Endpoint:** `/c/u/all`
    - **Method:** GET
    - **Description:** Получить список каналов пользователя.

# Flask Service Endpoints

## Video Processing
**Port**: `8642`

- **Endpoint:** `/process`
    - **Method:** POST
    - **Description:** Отправить видео на обработку (служебный).

- **Endpoint:** `/progress`
    - **Method:** GET
    - **Description:** Получить информацию о ходе обработки видео.

- **Endpoint:** `/result`
    - **Method:** GET
    - **Description:** Получить результаты обработки видео (служебный).

- **Endpoint:** `/video`
    - **Method:** GET
    - **Description:** Получить видеозапись после обработки (такая же ссылка есть в сериализованном video).
