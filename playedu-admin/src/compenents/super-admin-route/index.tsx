import React from "react";
import { useSelector } from "react-redux";
import { Navigate } from "react-router-dom";
import { getToken } from "../../utils/index";

interface PropInterface {
  Component: any;
}

/** Allows the points operations route only after the backend confirms super-admin status. */
const SuperAdminRoute: React.FC<PropInterface> = ({ Component }) => {
  const user = useSelector((state: any) => state.loginUser.value.user);
  const isSuperAdmin = useSelector(
    (state: any) => state.loginUser.value.isSuperAdmin
  );

  if (!getToken()) {
    return <Navigate to="/login" replace={true} />;
  }
  if (!user) {
    return null;
  }
  return isSuperAdmin ? Component : <Navigate to="/" replace={true} />;
};

export default SuperAdminRoute;
