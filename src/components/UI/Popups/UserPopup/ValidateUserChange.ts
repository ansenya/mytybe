import { IUser } from "../../../../models";

export const validateUsername = (existingUsers: IUser[], value: string) => {
  const pattern = /^[a-zA-Z0-9]+$/;
  if (value.length > 100)
    return "Имя пользователя должно быть не длинее 100 символов";
  else if (value.length == 0) return "Поле обязательно к заполнению";
  else if (!pattern.test(value))
    return "Можно использовать только латинские буквы и цифры";
  else if (existingUsers.map((user) => user.username).includes(value)) {
    return "Имя уже занято";
  }

  return "";
};
export const fileValidationError = (
  file: File | null,
  requiredFileType: "image" | "video",
  requiredSize?: number,
) => {
  let error = "";
  if (file === null) return "File is required";
  if (requiredFileType !== file.type.split("/")[0])
    error = "Недопустимый формат файла";
  if (requiredSize && file.size > requiredSize) error = "Слишком тяжелый файл";
  return error;
};
