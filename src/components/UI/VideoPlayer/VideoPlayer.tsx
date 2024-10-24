import React, {useCallback, useEffect, useRef, useState} from "react";
import Loader from "../Loader/Loader";
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
import PlayerSelect from "../PlayerSelect/PlayerSelect";
import usePlayerKeys from "../../../hooks/usePlayerKeys";
import {Settings} from "lucide-react";
import {IVideo} from "../../../models";
import axios from "axios";


interface VideoPlayerProps {
    video: IVideo,
    frame?: boolean
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({video, frame}) => {
    const playerRef = useRef<ReactPlayer>(null);
    const lineRef = useRef<HTMLInputElement>(null);

    const [isPlaying, setIsPlaying] = useState(false);
    const [isFullScreen, setIsFullScreen] = useState(false);
    const [volume, setVolume] = useState(
        Number(localStorage.getItem("volume")) || 1,
    );
    const [savedVolume, setSavedVolume] = useState(1);
    const [total, setTotal] = useState("0:00");
    const [current, setCurrent] = useState("0:00");
    const [progress, setProgress] = useState(0);
    const [savedProgress, setSavedProgress] = useState(0);
    const [playbackSpeed, setPlaybackSpeed] = useState<string>("1");
    const [isPip, setIsPip] = useState(false);
    const [isEnded, setIsEnded] = useState(false);
    const [isReady, setIsReady] = useState(false);
    const [qualities, setQualities] = useState<string[]>([]);
    const [quality, setQuality] = useState<string>("");
    const [timer, setTimer] = useState<ReturnType<typeof setTimeout>>();
    const [showControls, setShowControls] = useState(false);
    const [isError, setIsError] = useState<boolean>(false);
    const [isMenuShown, setIsMenuShown] = useState(false);

    const {
        isPlayPressed,
        isMutePressed,
        isForwardPressed,
        isBackwardPressed,
        isFullscreenPressed,
    } = usePlayerKeys();

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

    useEffect(() => {
        fetchQualitiesFromMasterPlaylist()
            .then(() => {
                console.log(qualities)
                console.log(quality)
            }
        );
    }, []);

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
        localStorage.setItem("volume", String(newVolume));
    }

    function handlePause() {
        setIsPlaying((prevState) => !prevState);
        setIsEnded(false)
    }

      const handleProgress = (state: { played: number; playedSeconds: number }) => {
        setProgress(state.played);
        if (savedProgress) {
            setProgress(savedProgress);
            playerRef.current?.seekTo(
                savedProgress * playerRef.current?.getDuration(),
            );
            setSavedProgress(0);
        }
        setIsError(false);
        setTotal(formatTime(playerRef.current?.getDuration() || 0));
        setCurrent(formatTime(state.playedSeconds));
    };

    function setPlayIcon() {
        if (isEnded) return RepeatIcon;
        if (!isPlaying) {
            return PlayIcon;
        }
        if (isPlaying) {
            return PauseIcon
        }
    }

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
        const newProgress = Number(e.target.value);
        setProgress(newProgress);
        setTotal(formatTime(playerRef.current?.getDuration() || 0));
        setCurrent(
            formatTime(newProgress * (playerRef.current?.getDuration() || 0)),
        );

        debounceSeek(0)(newProgress);
    };
    const handleControlsClick = (e: React.MouseEvent) => {
        e.stopPropagation();
    };

    function handleMouseMove() {
        setShowControls(true);
        clearTimeout(timer);
        let newTimer = setTimeout(() => {
            setShowControls(false);
        }, 3000);
        setTimer(newTimer);
    }

    function handleMouseClick() {
        handlePause();
        setShowControls(true);
        clearTimeout(timer);
        let newTimer = setTimeout(() => {
            setShowControls(false);
        }, 3000);
        setTimer(newTimer);
    }

    useEffect(() => {
        setSavedProgress(progress);
    }, [quality]);


    const fetchQualitiesFromMasterPlaylist = async () => {
        try {
            const response = await axios.get(video.path);
            const masterPlaylist = response.data;

            const parsedQualities = parseQualities(masterPlaylist);
            setQualities(parsedQualities);

            if (parsedQualities.length > 0) setQuality(parsedQualities[parsedQualities.length - 1]);
        } catch (error) {
            console.error("Error fetching master playlist:", error);
            setIsError(true);
        }
    };

    const parseQualities = (playlist: string) => {
        const qualityRegex = /#EXT-X-STREAM-INF:.*RESOLUTION=\d+x(\d+),.*\n(.*)\n/gi;
        let match;
        const foundQualities: string[] = [];

        while ((match = qualityRegex.exec(playlist)) !== null) {
            const qualityValue = match[1];
            foundQualities.push(qualityValue);
        }

        return foundQualities;
    };

    return (
        <div
            id="player-container"
            className={[
                "player",
                !isPlaying || showControls ? "controls-active" : "",
            ].join(" ")}
            onClick={handleMouseClick}
            onTouchStart={handleMouseClick}
            onMouseEnter={() => setShowControls(true)}
            onMouseLeave={() => setShowControls(false)}
            onMouseMove={handleMouseMove}>
                {!isError && (qualities.length !== 0) && (
                    <ReactPlayer
                        className="player__video"
                        url={`${video.path}?quality=${quality}`}
                        playing={isPlaying && isReady && !isError}
                        onReady={() => {
                            setIsReady(true);
                        }}
                        onPlay={() => {
                            setIsPlaying(true);
                        }}
                        onPause={() => {
                            setIsPlaying(false);
                        }}
                        onError={(error) => {
                            console.error('Error:', error);
                            setIsError(true);
                        }}
                        onEnded={() => {
                            setIsEnded(true);
                        }}
                        onProgress={handleProgress}
                        playbackRate={Number(playbackSpeed)}
                        progressInterval={5}
                        pip={isPip}
                        onDisablePIP={() => setIsPip(false)}
                        ref={playerRef}
                        muted={volume === 0}
                        controls={false}
                        width="100%"
                        height="auto"
                    />
                )}

            {!isReady && (
                <div className="loader">
                    <Loader/>
                </div>
            )}

            {isError && (
                <div className="error">
                    <h1>Couldn't load media</h1>
                </div>
            )}

            {frame || isFullScreen && (
                <div className="title-container">
                    <p>
                        {`${video.name} - ${video.channel.name}`.length > 40
                            ? `${video.name} - ${video.channel.name}`.substring(0, 39) + "..."
                            : `${video.name} - ${video.channel.name}` ?? ""}
                    </p>
                </div>)
            }
            <div className="controls-container" onClick={handleControlsClick}>
                <PlayerSelect
                    isShown={isMenuShown}
                    setIsShown={setIsMenuShown}
                    qualitiesAvailible={qualities}
                    quality={quality}
                    playbackSpeed={playbackSpeed}
                    setPlaybackSpeed={setPlaybackSpeed}
                    setQuality={setQuality}
                />
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
                        <div className="current">{current}</div>/<div className="total">{total}</div>
                    </div>
                    <button
                        className="controls__button"
                        onClick={() => setIsMenuShown((state) => !state)}>
                        <Settings size={25} color={"white"}/>
                    </button>
                    <PlayerButton icon={PipIcon} onClick={() => setIsPip(true)}/>
                    <PlayerButton
                        icon={isFullScreen ? ExitIcon : FullIcon}
                        onClick={handleFullScreen}
                    ></PlayerButton>
                </div>
            </div>
        </div>
    )
};

export default VideoPlayer;
