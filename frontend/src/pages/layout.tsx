import React from 'react';
import {Outlet, NavLink, useNavigate} from 'react-router-dom'
import {useActions} from "../hooks/actions";

const Layout = () => {

    const navigate = useNavigate()

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



                    <div>

                        {localStorage.getItem("username") ? (
                            <div>
                                {localStorage.getItem("username")}
                                <button className="button secondary" onClick={() => clearAuth()}>Sign out</button>
                            </div>
                        ) : (
                            <button className="button primary" onClick={() => navigate("/login")}>Sign in</button>
                        )}

                    </div>

                </nav>
            </header>
            <main>
                <Outlet/>
            </main>
        </div>
    );
};

export default Layout;