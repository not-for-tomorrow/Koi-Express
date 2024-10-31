import React from "react";
import { useRoutes } from "react-router-dom";
import MainLayout from "./components/MainLayout/MainLayout";
import HomePages from "./pages/HomePages/HomePages";
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import AppHomePages from "./pages/AppHomePages/AppHomePages";
import Salepage from "./Salepage/Salepage";
import DeliveringStaffpage from "./DeliveringStaffpage/DeliveringStaffpage";
import ManagerPage from "./ManagerPage/ManagerPage";
import LoginLayout from "./components/LogResLayout/LogResLayout";
import RegisterLayout from "./components/LogResLayout/LogResLayout";
import Blog from "./pages/Blog/Blog";

export default function useRouteElements() {
  const routeElements = useRoutes([
    {
      path: "/",
      element: (
        <MainLayout>
          <HomePages />
        </MainLayout>
      ),
    },
    {
      path: "/login",
      element: (
        <LoginLayout>
          <Login />
        </LoginLayout>
      ),
    },
    {
      path: "/register",
      element: (
        <RegisterLayout>
          <Register />
        </RegisterLayout>
      ),
    },
    {
      path: "/appkoiexpress/*",
      element: <AppHomePages />,
    },
    {
      path: "/salepage/*",
      element: <Salepage />,
    },
    {
      path: "/deliveringstaffpage/*",
      element: <DeliveringStaffpage />,
    },
    {
      path: "/managerpage/*",
      element: <ManagerPage />,
    },
    {
      path: "/blog/*",
      element: (
        <MainLayout>
          <Blog />
        </MainLayout>
      ),
    },
  ]);
  return routeElements;
}
