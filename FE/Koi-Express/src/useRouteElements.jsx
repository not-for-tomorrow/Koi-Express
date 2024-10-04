import React from "react";
import { useRoutes } from "react-router-dom";
import MainLayout from "./components/MainLayout/MainLayout";
import HomePages from "./pages/HomePages/HomePages";
import Login from "./pages/Login/Login";
import Register from "./pages/Register/Register";

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
        <MainLayout>
          <Login/>
        </MainLayout>
      ),
    },
    {
      path: "/register",
      element: (
        <MainLayout>
          <Register/>
        </MainLayout>
      ),
    },
  ]);
  return routeElements;
}
