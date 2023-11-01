import React from 'react';
import menuIcon from "../assets/menu-svgrepo-com (1) 1.svg"
import searchIcon from "../assets/search-alt-svgrepo-com (3) 1.svg"

const Navbar = () => {
    return (
        <div className="navbar">
            <div className="navbar__menu">
                <img src={menuIcon} alt="иконочка))" draggable={false}/>
                <span>MyTubeVideo</span>
            </div>
            <div className="navbar__center">
                <div className="search__bar">
                    <img src={searchIcon} alt="иконочка))" draggable={false}/>
                    <input type="text"/>
                </div>
            </div>
            <div className="btns">
                <button className="btns__signin">Sign in</button>
                <button className="btns__signup">Sign out</button>
            </div>
        </div>
    );
};

export default Navbar;