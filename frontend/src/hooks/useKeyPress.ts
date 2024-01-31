import { useState, useEffect } from 'react';

const useKeyPress = (targetKey: string, callback: () => void): void => {

    const downHandler = (event: KeyboardEvent) => {
        event.preventDefault()
        if (event.key === targetKey) {
            callback()
        }
    };

    useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => downHandler(event);

        window.addEventListener('keydown', handleKeyDown);

        return () => {
            window.removeEventListener('keydown', handleKeyDown);
        };
    }, [targetKey]);

};

export default useKeyPress;
