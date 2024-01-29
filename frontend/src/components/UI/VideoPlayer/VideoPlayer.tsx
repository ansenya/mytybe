import React, {useEffect, useRef, useState} from 'react';
import ReactPlayer from 'react-player';
import PlayerButton from "./PlayerButton";
import './VideoPlayer.scss'
import PlayIcon from "../../../assets/play-svgrepo-com (1).svg"
import PauseIcon from "../../../assets/pause-svgrepo-com.svg"
import FullIcon from "../../../assets/fullscreen-svgrepo-com (1).svg"
import ExitIcon from "../../../assets/fullscreen-exit-svgrepo-com.svg"
import LowVolume from "../../../assets/volume-low-filled-svgrepo-com.svg"
import HighVolume from "../../../assets/volume-filled-svgrepo-com.svg"
import MutedVolume from "../../../assets/volume-mute-filled-svgrepo-com.svg"


interface VideoPlayerProps {
    source: string;
}

const VideoPlayer: React.FC<VideoPlayerProps> = ({source}) => {
    const playerRef = useRef<ReactPlayer>(null);
    const [isPlaying, setIsPlaying] = useState(false);
    const [isFullScreen, setIsFullScreen] = useState(false);
    const [volume, setVolume] = useState(1);
    const [savedVolume, setSavedVolume] = useState(1);
    const [total, setTotal] = useState("0:00")
    const [current, setCurrent] = useState("0:00")
    const [progress, setProgress] = useState(0)

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

    function handlePause(e: React.MouseEvent){
        setIsPlaying(prevState => !prevState)
    }

    const handleProgress = (state: { playedSeconds: number; }) => {
        setProgress(playerRef.current?.getDuration()||0/state.playedSeconds)
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


    function handleMute(e: React.MouseEvent) {
        if (volume){
            setSavedVolume(volume)
            setVolume(0)
            return
        }
        setVolume(savedVolume)
    }

    return (
        <div id="player-container" className={["player", !isPlaying ? "paused" : ""].join(" ")}>
            <ReactPlayer
                playing={isPlaying}
                volume={volume}
                className="player__video"
                ref={playerRef}
                url={source}
                width="100%"
                height="auto"
                onProgress={handleProgress}
            />
            <div className="controls-container">
                <div className="timeline-container">
                    <input type="range" min={0} max={1} step="any"  className="timeline" value={progress}/>
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
                    <PlayerButton icon={isFullScreen ? ExitIcon : FullIcon} onClick={handleFullScreen}></PlayerButton>

                </div>
            </div>
        </div>
    );
};

export default VideoPlayer;