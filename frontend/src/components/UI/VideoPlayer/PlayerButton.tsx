import React, {ButtonHTMLAttributes, FC} from 'react';

interface iconProp {
    icon: string;
}

type props = ButtonHTMLAttributes<HTMLButtonElement> & iconProp
const PlayerButton: FC<props> = ({icon, ...props}) => {
    return (
        <button {...props} className={["controls__button", props.className].join(" ")}>
            <img src={icon} alt="" draggable={false}/>
        </button>
    );
};

export default PlayerButton;