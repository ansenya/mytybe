import React, {useEffect, useRef, useState} from 'react';
import ReactPlayer from 'react-player';
import PlayerButton from "./PlayerButton";
import './VideoPlayer.scss'
import PipIcon from "../../../assets/pip-svgrepo-com.svg"
import PlayIcon from "../../../assets/play-svgrepo-com (1).svg"
import PauseIcon from "../../../assets/pause-svgrepo-com.svg"
import FullIcon from "../../../assets/fullscreen-svgrepo-com (1).svg"
import ExitIcon from "../../../assets/fullscreen-exit-svgrepo-com.svg"
import LowVolume from "../../../assets/volume-low-filled-svgrepo-com.svg"
import HighVolume from "../../../assets/volume-filled-svgrepo-com.svg"
import MutedVolume from "../../../assets/volume-mute-filled-svgrepo-com.svg"
import Loader from "../Loader/Loader";
import Hls from "hls.js";
import useKeyPress from "../../../hooks/useKeyPress";



interface VideoPlayerProps {
    source: string;
}


const VideoPlayer: React.FC<VideoPlayerProps> = ({source}) => {
    const playerRef = useRef<ReactPlayer>(null);
    const lineRef = useRef<HTMLInputElement>(null);

    const [isPlaying, setIsPlaying] = useState(true);
    const [isFullScreen, setIsFullScreen] = useState(false);
    const [volume, setVolume] = useState(1);
    const [savedVolume, setSavedVolume] = useState(1);
    const [total, setTotal] = useState("0:00")
    const [current, setCurrent] = useState("0:00")
    const [progress, setProgress] = useState(0)
    const [isLoading, setIsLoading] = useState(true)
    const [playbackSpeed, setPlaybackSpeed] = useState(1)
    const [isPip, setIsPip] = useState(false);

    const seekForward = () => {
        console.log("f");
    }

    const seekBackward = () => {
        console.log("b");
    }

    useKeyPress(" ", handlePause);
    useKeyPress("k", handlePause);
    useKeyPress("m", handleMute);
    useKeyPress("j", seekForward);
    useKeyPress("l", seekBackward);
    useKeyPress("ArrowRight", seekForward);
    useKeyPress("ArrowLeft", seekForward);

    const formatTime = (time: number) => {
        let seconds = Math.floor(time)%60;
        let minutes = Math.floor(time/60)%60;
        let hours = Math.floor(time/3600)

        if (hours) return `${hours}:${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
        return `${minutes}:${String(seconds).padStart(2, '0')}`
    }

    const setVolumeIcon: () => string = () => {
        if (volume >= 0.5) return HighVolume;
        else if (volume === 0) return MutedVolume;
        return LowVolume;
    }

    function handleVolumeChange(e: React.ChangeEvent<HTMLInputElement>){
        const newVolume = parseFloat(e.target.value)
        setVolume(newVolume)
    }



    function handlePause(){
        setIsPlaying(prevState => !prevState);
    }

    const handleProgress = (state: {played: number, playedSeconds: number}) => {
        console.log(progress*(playerRef.current?.getDuration()||0) - state.playedSeconds);
        if (progress > state.played){
            playerRef.current?.seekTo(Number(progress*playerRef.current?.getDuration()));
        }
        setProgress(state.played)
        setTotal(formatTime(playerRef.current?.getDuration() || 0));
        setCurrent(formatTime(state.playedSeconds));
    };



    const handleFullScreen = () => {
        if (!isFullScreen) {
            const playerContainer = document.getElementById('player-container');
            if (playerContainer) {
                playerContainer.requestFullscreen();
            }
        } else {
            document.exitFullscreen();
        }
    };

    // Detect fullscreen change
    useEffect(() => {
        const handleFullscreenChange = () => {
            setIsFullScreen(!!document.fullscreenElement);
        };

        document.addEventListener('fullscreenchange', handleFullscreenChange);

        return () => {
            document.removeEventListener('fullscreenchange', handleFullscreenChange);
        };
    }, []);

    useEffect(() => {
        if (lineRef.current){
            lineRef.current.style.background = `linear-gradient(to right, #6002ee, #6002ee ${progress*100+0.05}%, white ${progress*100}%, white 100%)`
        }
    }, [progress])



    const handleSeek = (e: React.MouseEvent<HTMLInputElement> & React.ChangeEvent<HTMLInputElement>) => {
        setProgress(Number(e.target.value))
        setTotal(formatTime(playerRef.current?.getDuration() || 0));
        setCurrent(formatTime(Number(e.target.value)*(playerRef.current?.getDuration()||0)));
        playerRef.current?.seekTo(Number(e.target.value)*playerRef.current?.getDuration())
    }



    const handleMouseDown = () => {
        lineRef.current?.addEventListener('mousemove', () => handleSeek);
        setIsPlaying(false);
    };

    const handleMouseUp = () => {
        lineRef.current?.removeEventListener('mousemove', () => handleSeek);
        if (!isLoading) setIsPlaying(true);
    };

    function handleMute() {
        if (volume){
            setSavedVolume(volume)
            setVolume(0)
            return
        }
        setVolume(savedVolume)
    }

    const handlePip = () => {
        setIsPip(true);
    }

    const handleBufferStart = () => {
        setIsLoading(true)
    }

    const handleBufferEnd = () => {
        setIsLoading(false)
        setIsPlaying(true);
    }

    const handleControlsClick = (e: React.MouseEvent) => {
        e.stopPropagation();
    }

    return (
        <div id="player-container" className={["player", !isPlaying ? "paused" : ""].join(" ")} onClick={handlePause}>
            <ReactPlayer
                playing={isPlaying}
                volume={volume}
                progressInterval={100}
                className="player__video"
                ref={playerRef}
                url={source}
                width="100%"
                height="auto"
                onProgress={handleProgress}
                onReady={() => setIsLoading(false)}
                playbackRate={playbackSpeed}
                onBuffer={handleBufferStart}
                onBufferEnd={handleBufferEnd}
                pip={isPip}
                onDisablePIP={() => setIsPip(false)}
            />
            {isLoading && (<div className="loader"><Loader/></div>)}
            <div className="controls-container" onClick={handleControlsClick}>
                <div className="timeline-container">
                    <input type="range" min={0} max={1} step="any"  className="timeline" value={progress} ref={lineRef}
                        onChange={handleSeek} onMouseDown={handleMouseDown} onMouseUp={handleMouseUp}
                    />
                </div>
                <div className="controls">
                    <PlayerButton icon={!isPlaying ? PlayIcon : PauseIcon} onClick={handlePause}></PlayerButton>
                    <div className="volume-container">
                        <PlayerButton icon={setVolumeIcon()} onClick={handleMute}></PlayerButton>
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
                        <div className="current">{current}</div>
                        /
                        <div className="total">{total}</div>
                    </div>
                    <PlayerButton
                        onClick={(e) => {
                            setPlaybackSpeed(prevState => prevState%2+0.25)
                        }}
                        text={`${playbackSpeed}x`}
                        >
                    </PlayerButton>
                    <PlayerButton icon={PipIcon} onClick={handlePip}></PlayerButton>
                    <PlayerButton icon={isFullScreen ? ExitIcon : FullIcon} onClick={handleFullScreen}></PlayerButton>

                </div>
            </div>
        </div>
    );
};

export default VideoPlayer;