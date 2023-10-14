## Spring Service Endpoints

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
  - **Description:** Получить список всех видеозаписей (можно получить только по id канала. описание - в json postman).

- **Endpoint:** `/v/tag`
  - **Method:** GET
  - **Description:** Получить видеозаписи по тегам (служебный).

- **Endpoint:** `/v/video`
  - **Method:** GET
  - **Description:** Получить информацию о видеозаписи.

- **Endpoint:** `/v/video`
  - **Method:** POST
  - **Description:**  Загрузить видео.

## Channel Endpoints
- **Endpoint:** `/c/all`
  - **Method:** GET
  - **Description:** Получить список всех каналов (можно получить только по id пользователя. описание - в json postman).

- **Endpoint:** `/c/channel`
  - **Method:** GET
  - **Description:** Получить информацию о канале.

- **Endpoint:** `/c/channel`
  - **Method:** POST
  - **Description:** Создать новый канал.


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
