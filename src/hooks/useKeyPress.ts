import { useState, useEffect } from 'react';

const useKeyPress = (targetKeys: string[], callback: Function, ...args: any[]): void => {

    const downHandler = (event: KeyboardEvent) => {
        if (event.key === " ") event.preventDefault();
        if (targetKeys.includes(event.key.toLowerCase())){
            console.log(args)
            callback(args)
        }
    };

    useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => downHandler(event);

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, []);

};

export default useKeyPress;
