export const lengthValidationError = (name: string, maxLen: number) => {
  let error = "";
  if (name.length > maxLen) error = `Максимальная длина ${maxLen}`;
  if (name.length === 0) error = "Поле обязательно к заполнению";
  return error;
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
  if (requiredSize && file.size > requiredSize)
    error = "Слишком тяжелый файл";
  return error;
};
