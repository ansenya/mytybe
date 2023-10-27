import React from 'react';
import {Outlet, NavLink} from 'react-router-dom'

const Layout = () => {
    return (
        <div>
            <header>
                <nav className="navbar">
                    <span className="logo">MyTube</span>

                    <ul className="navlinks">
                        <li className="nav__item"><NavLink to="/">videos</NavLink></li>
                        <li className="nav__item"><NavLink to="channels">channels</NavLink></li>
                        <li className="nav__item"><NavLink to="users">users</NavLink></li>
                    </ul>

                    <button className="button primary">Sign in</button>
                    <button className="button secondary">Sign up</button>

                </nav>
            </header>
            <main>
                <Outlet/>
            </main>
        </div>
    );
};

export default Layout;