import React, { FC, SelectHTMLAttributes, useEffect, useState } from "react";
import PlayerButton from "../VideoPlayer/PlayerButton";
import styles from "./PlayerSelect.module.scss";
import settingsIcon from "../../../assets/settings-svgrepo-com.svg";
import speedIcon from "../../../assets/speed-fill-svgrepo-com.svg";
import hdIcon from "../../../assets/hd-svgrepo-com.svg";
import tickIcon from "../../../assets/checkmark-svgrepo-com.svg";
import { Settings } from "lucide-react";
import { FastForward } from "lucide-react";
import { Tv2 } from "lucide-react";
import { Check } from "lucide-react";

interface PlayerSelectProps {
  qualitiesAvailible: string[];
  quality: string;
  playbackSpeed: string;
  setQuality: Function;
  setPlaybackSpeed: Function;
  isShown: boolean;
  setIsShown: Function;
}

interface IMenu {
  [key: string]: React.Dispatch<React.SetStateAction<boolean>>;
}

const PlayerSelect: FC<PlayerSelectProps> = ({
  setIsShown,
  isShown,
  qualitiesAvailible,
  quality,
  playbackSpeed,
  setQuality,
  setPlaybackSpeed,
}) => {
  const [isQualityOpened, setIsQualityOpened] = useState<boolean>(false);
  const [isSpeedOpened, setIsSpeedOpened] = useState<boolean>(false);
  let timer: NodeJS.Timer;

  const handleSpeedChoice = (event: React.MouseEvent<HTMLDivElement>) => {
    setIsShown(false);
    setIsSpeedOpened(false);
    setPlaybackSpeed(event.currentTarget.dataset.value);
  };

  const handleQualityChoice = (event: React.MouseEvent<HTMLDivElement>) => {
    setIsShown(false);
    setIsQualityOpened(false);
    setQuality(event.currentTarget.dataset.value);
  };

  const menu: IMenu = {
    Разрешение: setIsQualityOpened,
    Скорость: setIsSpeedOpened,
  };
  const qValues = [...qualitiesAvailible].reverse();
  const pValues = ["2", "1.5", "1", "0.5"];

  return (
    <div className={[styles.container, isShown ? styles.active : ""].join(" ")}>
      <div
        className={[styles.options, isShown ? styles.active : ""].join(" ")}
        onClick={(e) => e.stopPropagation()}
      >
        {!isSpeedOpened &&
          !isQualityOpened &&
          Object.keys(menu).map((option: string, index: number) => (
            <div
              className={styles.longOption}
              onClick={() => menu[option as string](true)}
              key={index}
            >
              <div className={styles.name}>
                {option === "Разрешение" ? (
                  <Tv2 size={20} color={"white"} />
                ) : (
                  <FastForward size={20} color={"white"} />
                )}
                <div>{option}</div>
              </div>
              <span>
                {option === "Разрешение" ? `${quality}p` : `${playbackSpeed}x`}
              </span>
            </div>
          ))}
        {isQualityOpened &&
          qValues.map((option: string, index: number) => (
            <div
              className={[
                styles.option,
                option === quality ? styles.chosen : "",
              ].join(" ")}
              data-value={option}
              onClick={handleQualityChoice}
              key={index}
            >
              <div>
                {option === quality && (
                  <Check color={"white"} size={20} strokeWidth={3} />
                )}
              </div>
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
              <div>
                {option === playbackSpeed && (
                  <Check color={"white"} size={20} strokeWidth={3} />
                )}
              </div>
              <span>{option}x</span>
            </div>
          ))}
      </div>
    </div>
  );
};

export default PlayerSelect;
