import React from "react";
import { useRoutes } from "react-router-dom";
import MainLayout from "./components/MainLayout/MainLayout";
import HomePages from "./pages/HomePages/HomePages";
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";
import AppHomePages from "./pages/AppHomePages/AppHomePages";
import OtpModal from "./pages/OTP/OtpModal";
import Salepage from "./Salepage/Salepage";
import DeliveringStaffpage from "./DeliveringStaffpage/DeliveringStaffpage";
import ManagerPage from "./ManagerPage/ManagerPage";

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
      element: <Login />,
    },
    {
      path: "/register",
      element: <Register />,
    },
    {
      path: "/appkoiexpress/*",
      element: <AppHomePages />,
    },
    {
      path: "/verify-otp",
      element: <OtpModal />,
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
  ]);
  return routeElements;
}
