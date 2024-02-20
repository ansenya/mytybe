import { useState, useEffect } from "react";

const useKeyPress = (targetKeys: string[] ): boolean => {
  const [isPressed, setIsPressed] = useState<boolean>(false);

  const downHandler = (event: KeyboardEvent) => {
    if (event.key === " ") event.preventDefault();
    if (targetKeys.includes(event.key.toLowerCase())) {
      setIsPressed(true);
    }
  };

  const upHandler = (event: KeyboardEvent) => {
    if ( ["ArrowRigth", "ArrowLeft", " "].includes(event.key)) event.preventDefault();
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
  }, []);

  return isPressed;
};


export default useKeyPress;
