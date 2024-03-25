import React from "react";
import { Routes, Route } from "react-router-dom";
import Layout from "./pages/layout";
import VideosPage from "./pages/videosPage";
import ChannelsPage from "./pages/channelsPage";
import UsersPage from "./pages/usersPage";
import UserPage from "./pages/userPage";
import VideoPage from "./pages/videoPage/videoPage";
import ChannelPage from "./pages/channelPage";
import ErrorPage from "./pages/errorPage";
import RegistrationPage from "./pages/authPages/registrationPage";
import LoginPage from "./pages/authPages//loginPage";
import RequireAuth from "./hoc/RequireAuth";
import Logout from "./pages/logout";
import UploadPage from "./pages/uploadPage/uploadPage";
import BlockAuth from "./hoc/BlockAuth";

import "./App.scss";

const App = () => {
  return (
    <>
      <Routes>
        <Route path="/" element={<Layout></Layout>}>
          <Route index element={<VideosPage />}></Route>
          <Route
            path="login"
            element={
              <BlockAuth>
                <LoginPage />
              </BlockAuth>
            }
          ></Route>
          <Route path="logout" element={<Logout />}></Route>
          <Route
            path="register"
            element={
              <BlockAuth>
                <RegistrationPage />
              </BlockAuth>
            }
          ></Route>
          <Route path="videos/upload" element={<UploadPage />}></Route>
          <Route path="channels" element={<ChannelsPage />}></Route>
          <Route path="channels/:id" element={<ChannelPage />}></Route>
          <Route path="videos/:id" element={<VideoPage />}></Route>
          <Route path="users" element={<UsersPage />}></Route>
          <Route path="users/:id" element={<UserPage />}></Route>
          <Route path="*" element={<ErrorPage />}></Route>
        </Route>
      </Routes>
    </>
  );
};

export default App;
