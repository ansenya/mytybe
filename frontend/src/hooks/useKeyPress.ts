import { useState, useEffect } from 'react';

const useKeyPress = (targetKeys: string[], callback: () => void): void => {

    const downHandler = (event: KeyboardEvent) => {
        if (event.key === " ") event.preventDefault();
        if (targetKeys.includes(event.key.toLowerCase())){
            callback()
        }
    };

    useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => downHandler(event);

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

};

export default useKeyPress;
