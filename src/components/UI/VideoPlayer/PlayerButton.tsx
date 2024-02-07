import React, {ButtonHTMLAttributes, FC} from 'react';

interface iconProp {
    icon?: string;
    text?: string;
}

type props = ButtonHTMLAttributes<HTMLButtonElement> & iconProp
const PlayerButton: FC<props> = ({icon, text, ...props}) => {
    return (
        <button {...props} className={["controls__button", props.className].join(" ")}>
            {!!(icon) ? <img src={icon} alt="" draggable={false}/> : text}
        </button>
    );
};

export default PlayerButton;