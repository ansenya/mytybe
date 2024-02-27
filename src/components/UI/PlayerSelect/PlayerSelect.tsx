import React, { FC, SelectHTMLAttributes, useEffect, useState } from "react";
import PlayerButton from "../VideoPlayer/PlayerButton";
import styles from "./PlayerSelect.module.scss";
import settingsIcon from "../../../assets/settings-svgrepo-com.svg";
import speedIcon from "../../../assets/speed-fill-svgrepo-com.svg";
import hdIcon from "../../../assets/hd-svgrepo-com.svg";
import tickIcon from "../../../assets/checkmark-svgrepo-com.svg";

interface PlayerSelectProps {
  quality: string;
  qualities: string[];
  playbackSpeed: string;
  setQuality: Function;
  setPlaybackSpeed: Function;
  qValues: string[]
}

interface IMenu {
  [key: string]: React.Dispatch<React.SetStateAction<boolean>>;
}

const PlayerSelect: FC<PlayerSelectProps> = ({
  quality,
  qualities,
  playbackSpeed,
  setQuality,
  setPlaybackSpeed,
    qValues
}) => {
  const [isMenuOpened, setIsMenuOpened] = useState<boolean>(false);
  const [isQualityOpened, setIsQualityOpened] = useState<boolean>(false);
  const [isSpeedOpened, setIsSpeedOpened] = useState<boolean>(false);
  let timer: NodeJS.Timer;

  const handleSpeedChoice = (event: React.MouseEvent<HTMLDivElement>) => {
    setIsMenuOpened(false);
    setIsSpeedOpened(false);
    setPlaybackSpeed(event.currentTarget.dataset.value);
  };

  const handleQualityChoice = (event: React.MouseEvent<HTMLDivElement>) => {
    setIsMenuOpened(false);
    setIsQualityOpened(false);
    setQuality(event.currentTarget.dataset.value);
  };

 const handleMenuTriggered = () => {
    setIsQualityOpened(false);
    setIsSpeedOpened(false);
    setIsMenuOpened(prevState => !prevState)
  }

  const menu: IMenu = {
    "Разрешение" : setIsQualityOpened,
    "Скорость": setIsSpeedOpened,
  };
  const pValues = ["2", "1.5", "1", "0.5"];

  return (
    <div className={styles.selectContainer}>
      <PlayerButton
        icon={settingsIcon}
        onClick={handleMenuTriggered}
      />
      <div
        className={[styles.options, isMenuOpened ? styles.active : ""].join(
          " ",
        )}
        onClick={(e) => e.stopPropagation()}
        onMouseLeave={() => {isMenuOpened ? timer = setTimeout(handleMenuTriggered, 2000) : console.log("penis")}}
        onMouseEnter={() => clearTimeout(timer)}
      >
        {!isSpeedOpened &&
          !isQualityOpened &&
          Object.keys(menu).map((option: string, index: number) => (
            <div
              className={styles.longOption}
              onClick={() => menu[option as string](true)}
              key={index}
            >
              <div><img src={option === "Разрешение" ? hdIcon : speedIcon} /> {option}</div>
              <span>{option === "Разрешение" ? `${quality}p` : `${playbackSpeed}x`}</span>
            </div>
          ))}
        {isQualityOpened && 
          qualities.map((option: string, index: number) => (
            <div
              className={[
                styles.option,
                option === quality ? styles.chosen : "",
              ].join(" ")}
              data-value={option}
              onClick={handleQualityChoice}
              key={index}
            >
              <div>{option === quality && <img src={tickIcon}/>}</div>
              <span>{option}p</span>
            </div>
          ))}
        {isSpeedOpened &&
          pValues.map((option: string, index: number) => (
            <div
              className={[
                styles.option,
                option === playbackSpeed ? styles.chosen : "",
              ].join(" ")}
              data-value={option}
              onClick={handleSpeedChoice}
              key={index}
            >
              <div>{option === playbackSpeed && <img src={tickIcon}/>}</div>
              <span>{option}x</span>
            </div>
          ))}
      </div>
    </div>
  );
};

export default PlayerSelect;
