import { useState, useEffect } from "react";
import { useAppSelector } from "./redux";

const useKeyPress = (targetKeys: string[]): boolean => {
  const [isPressed, setIsPressed] = useState<boolean>(false);
  const { isFocused } = useAppSelector((state) => state.focus);

  const downHandler = (event: KeyboardEvent) => {
    if (isFocused) return;
    if (event.key === " ") event.preventDefault();
    if (targetKeys.includes(event.key.toLowerCase())) {
      setIsPressed(true);
    }
  };

  const upHandler = (event: KeyboardEvent) => {
    if (isFocused) return;
    if (["ArrowRigth", "ArrowLeft", " "].includes(event.key))
      event.preventDefault();
    if (targetKeys.includes(event.key.toLowerCase())) {
      setIsPressed(false);
    }
  };

  useEffect(() => {
    const handleKeyDown = (event: KeyboardEvent) => downHandler(event);

    document.addEventListener("keydown", downHandler);
    document.addEventListener("keyup", upHandler);

    return () => {
      document.removeEventListener("keydown", downHandler);
      document.removeEventListener("keyup", upHandler);
    };
  }, [isFocused]);

  return isPressed;
};

export default useKeyPress;
