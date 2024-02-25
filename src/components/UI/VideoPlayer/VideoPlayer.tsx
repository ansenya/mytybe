import React, { useCallback, useEffect, useRef, useState } from "react";
import Loader from "../Loader/Loader";
import Hls from "hls.js";
import useKeyPress from "../../../hooks/useKeyPress";
import ReactPlayer from "react-player";
import PlayerButton from "./PlayerButton";
import "./VideoPlayer.scss";

import PipIcon from "../../../assets/pip-svgrepo-com.svg";
import PlayIcon from "../../../assets/play-svgrepo-com (1).svg";
import PauseIcon from "../../../assets/pause-svgrepo-com.svg";
import FullIcon from "../../../assets/fullscreen-svgrepo-com (1).svg";
import ExitIcon from "../../../assets/fullscreen-exit-svgrepo-com.svg";
import LowVolume from "../../../assets/volume-low-filled-svgrepo-com.svg";
import HighVolume from "../../../assets/volume-filled-svgrepo-com.svg";
import MutedVolume from "../../../assets/volume-mute-filled-svgrepo-com.svg";
import RepeatIcon from "../../../assets/redo-icon-svgrepo-com.svg";
import ChipiChapa from "../../../assets/asdf.mp4";
import PlayerSelect from "../PlayerSelect/PlayerSelect";
import usePlayerKeys from "../../../hooks/usePlayerKeys";

interface VideoPlayerProps {
  source: string;
  qValues: string[];
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({ source, qValues }) => {
  const playerRef = useRef<ReactPlayer>(null);
  const lineRef = useRef<HTMLInputElement>(null);

  const [isPlaying, setIsPlaying] = useState(true);
  const [isFullScreen, setIsFullScreen] = useState(false);
  const [volume, setVolume] = useState(1);
  const [savedVolume, setSavedVolume] = useState(1);
  const [total, setTotal] = useState("0:00");
  const [current, setCurrent] = useState("0:00");
  const [progress, setProgress] = useState(0);
  const [isLoading, setIsLoading] = useState(true);
  const [playbackSpeed, setPlaybackSpeed] = useState<string>("1");
  const [isPip, setIsPip] = useState(false);
  const [isEnded, setIsEnded] = useState(false);
  const [quality, setQuality] = useState<string>(
    Math.max(...qValues.map((value: string) => parseInt(value))).toString(),
  );
  const [isError, setIsError] = useState<boolean>(false);

  const {
    isPlayPressed,
    isMutePressed,
    isForwardPressed,
    isBackwardPressed,
    isFullscreenPressed,
  } = usePlayerKeys();

  // keyboard actions
  useEffect(() => {
    if (isPlayPressed) handlePause();
    if (isForwardPressed) seekForward();
    if (isMutePressed) handleMute();
    if (isBackwardPressed) seekBackward();
    if (isFullscreenPressed) handleFullScreen();
  }, [
    isPlayPressed,
    isMutePressed,
    isForwardPressed,
    isBackwardPressed,
    isFullscreenPressed,
  ]);

  const seekForward = () => {
    if (playerRef.current)
      playerRef.current?.seekTo(playerRef.current?.getCurrentTime() + 10);
  };
  const seekBackward = () => {
    if (playerRef.current)
      playerRef.current?.seekTo(playerRef.current?.getCurrentTime() - 10);
  };

  const handleMute = () => {
    if (volume !== 0) {
      setSavedVolume(volume);
      setVolume(0);
    } else {
      setVolume(savedVolume);
    }
  };

  const handleFullScreen = () => {
    if (!isFullScreen) {
      const playerContainer = document.getElementById("player-container");
      if (playerContainer) {
        playerContainer.requestFullscreen();
      }
    } else {
      document.exitFullscreen();
    }
  };

  const formatTime = (time: number) => {
    let seconds = Math.floor(time) % 60;
    let minutes = Math.floor(time / 60) % 60;
    let hours = Math.floor(time / 3600);

    if (hours)
      return `${hours}:${String(minutes).padStart(2, "0")}:${String(
        seconds,
      ).padStart(2, "0")}`;
    return `${minutes}:${String(seconds).padStart(2, "0")}`;
  };

  const setVolumeIcon: () => string = () => {
    if (volume >= 0.5) return HighVolume;
    else if (volume === 0) return MutedVolume;
    return LowVolume;
  };

  function handleVolumeChange(e: React.ChangeEvent<HTMLInputElement>) {
    const newVolume = parseFloat(e.target.value);
    setVolume(newVolume);
  }

  function handlePause() {
    setIsEnded(false);
    setIsPlaying((prevState) => !prevState);
  }

  const handleProgress = (state: { played: number; playedSeconds: number }) => {
    if (isLoading) return;
    setProgress(state.played);
    setIsError(false);
    setTotal(formatTime(playerRef.current?.getDuration() || 0));
    setCurrent(formatTime(state.playedSeconds));
  };

  function setPlayIcon() {
    if (isEnded) return RepeatIcon;

    return !isPlaying ? PlayIcon : PauseIcon;
  }

  // Detect fullscreen change
  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullScreen(!!document.fullscreenElement);
    };

    document.addEventListener("fullscreenchange", handleFullscreenChange);

    return () => {
      document.removeEventListener("fullscreenchange", handleFullscreenChange);
    };
  }, []);

  useEffect(() => {
    if (lineRef.current) {
      lineRef.current.style.background = `linear-gradient(to right, #6002ee, #6002ee ${
        progress * 100 + 0.05
      }%, white ${progress * 100}%, white 100%)`;
    }
  }, [progress]);

  function debounceSeek(delay: number) {
    let timer: NodeJS.Timer;
    return function (rangeValue: number) {
      clearTimeout(timer);
      timer = setTimeout(() => {
        playerRef.current?.seekTo(
          rangeValue * playerRef.current?.getDuration(),
        );
      }, delay);
    };
  }

  const handleSeek = (
    e: React.MouseEvent<HTMLInputElement> & React.ChangeEvent<HTMLInputElement>,
  ) => {
    setProgress(Number(e.target.value));
    setTotal(formatTime(playerRef.current?.getDuration() || 0));
    setCurrent(
      formatTime(
        Number(e.target.value) * (playerRef.current?.getDuration() || 0),
      ),
    );
    debounceSeek(1000)(Number(e.target.value));
  };

  const onMouseDown = () => {
    setIsLoading(true);
  };

  const handlePip = () => {
    setIsPip(true);
  };

  const handleBufferStart = () => {
    setIsLoading(true);
  };

  const handleBufferEnd = () => {
    setIsLoading(false);
  };

  const handleControlsClick = (e: React.MouseEvent) => {
    e.stopPropagation();
  };

  return (
    <div
      id="player-container"
      className={["player", !isPlaying ? "paused" : ""].join(" ")}
      onClick={handlePause}
    >
      <ReactPlayer
        playing={isPlaying && !isLoading}
        onError={(error) => setIsError(true)}
        volume={volume}
        progressInterval={100}
        className="player__video"
        ref={playerRef}
        url={`${source}&q=${quality}`}
        width="100%"
        height="auto"
        onProgress={handleProgress}
        onSeek={() => setIsLoading(true)}
        onReady={() => setIsLoading(false)}
        playbackRate={Number(playbackSpeed)}
        onBuffer={handleBufferStart}
        onBufferEnd={handleBufferEnd}
        pip={isPip}
        onDisablePIP={() => setIsPip(false)}
        onEnded={() => {
          setIsEnded(true);
          setIsPlaying(false);
        }}
      />
      {isLoading && (
        <div className="loader">
          <Loader />
        </div>
      )}
      {isError && (
        <div className="error">
          <h1>Couldn't load media</h1>
        </div>
      )}
      <div className="controls-container" onClick={handleControlsClick}>
        <div className="timeline-container">
          <input
            type="range"
            min={0}
            max={1}
            step="any"
            className="timeline"
            value={progress}
            ref={lineRef}
            onMouseUp={handleSeek}
            onMouseDown={onMouseDown}
            onTouchStart={onMouseDown}
            onChange={handleSeek}
          />
        </div>
        <div className="controls">
          <PlayerButton
            icon={setPlayIcon()}
            onClick={handlePause}
          ></PlayerButton>
          <div className="volume-container">
            <PlayerButton
              icon={setVolumeIcon()}
              onClick={() => handleMute()}
            ></PlayerButton>
            <input
              type="range"
              min="0"
              max="1"
              step="0.01"
              value={volume}
              onChange={handleVolumeChange}
              className="volume-slider"
            />
          </div>
          <div className="duration">
            <div className="current">{current}</div>/
            <div className="total">{total}</div>
          </div>
          <PlayerButton icon={PipIcon} onClick={handlePip}></PlayerButton>
          <PlayerButton
            icon={isFullScreen ? ExitIcon : FullIcon}
            onClick={handleFullScreen}
          ></PlayerButton>
        </div>
      </div>
    </div>
  );
};

export default VideoPlayer;
