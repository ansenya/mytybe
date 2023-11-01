import React from 'react';
import {Outlet, NavLink, useNavigate} from 'react-router-dom'
import {useActions} from "../hooks/actions";
import Navbar from "../components/Navbar";


const Layout = () => {

    const navigate = useNavigate()

    const {clearAuth} = useActions()


    return (
        <div>
            <div className="header">
                <Navbar/>
            </div>
            <div className="main">
                <Outlet/>
            </div>
        </div>
    );
};

export default Layout;