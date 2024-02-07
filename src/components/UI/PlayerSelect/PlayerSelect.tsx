import React, {FC, SelectHTMLAttributes, useEffect, useState} from 'react';
import PlayerButton from "../VideoPlayer/PlayerButton";
import styles from "./PlayerSelect.module.scss"

interface addSelectProps {
    choice: string;
    options: string[];
    setChoice: Function;
    letter?: string;
}

type SelectProps = addSelectProps & SelectHTMLAttributes<HTMLSelectElement>

const PlayerSelect: FC<SelectProps> = ({letter,choice, options, setChoice, ...props}) => {
    const [isDropped, setIsDropped] = useState<boolean>(false);

    const handleChoice = (e: React.MouseEvent<HTMLDivElement>) => {
        const value = e.currentTarget.dataset.value;
        setChoice(value);
    }

    const handleDrop = () => {
        setIsDropped(true);

    }

    return (
        <div className={styles.selectContainer}>
            <PlayerButton text={`${choice}${letter}`} onClick={() => setIsDropped(prevState => !prevState)}/>
            <div className={[styles.options, isDropped ? styles.active : ""].join(" ")} onClick={(e) => e.stopPropagation()}>
                {options.map((option, index) => (
                    <div className={styles.option} data-value={option} onClick={handleChoice} key={index}>{`${option}${letter}`}</div>
                ))}
            </div>
        </div>
    );
};

export default PlayerSelect;