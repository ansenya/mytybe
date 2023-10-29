import React from 'react';
import {Outlet, NavLink} from 'react-router-dom'
import {useActions} from "../hooks/actions";

const Layout = () => {
    const {clearAuth} = useActions()

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
                    <button className="button secondary" onClick={() => clearAuth()}>Sign up</button>

                </nav>
            </header>
            <main>
                <Outlet/>
            </main>
        </div>
    );
};

export default Layout;