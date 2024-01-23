import React, {ButtonHTMLAttributes, FC} from 'react';

interface iconProp {
    icon: string;
}

type props = ButtonHTMLAttributes<HTMLButtonElement> & iconProp
const PlayerButton: FC<props> = ({icon, ...props}) => {
    return (
        <button {...props} className="controls__button">
            <img src={icon} alt="иконочка))" draggable={false}/>
        </button>
    );
};

export default PlayerButton;