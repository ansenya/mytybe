import useKeyPress from "./useKeyPress";

const usePlayerKeys = () => {
  return {
    isPlayPressed: useKeyPress([" ", "k", "л"]), 
    isMutePressed: useKeyPress(["m", "ь"]),
    isFullscreenPressed: useKeyPress(["f", "а"]),
    isForwardPressed: useKeyPress(["l", "д", "arrowright"]),
    isBackwardPressed: useKeyPress(["j", "о", "arrowleft"])
  }
}

export default usePlayerKeys; 
