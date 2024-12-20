import React from "react";
import {Route, Routes} from "react-router-dom";
import Sidebar from "../componentsDashboard/Salecomponents/Sidebar/Sidebar";
import Order from "./Order";
import CustomerAccount from "./CustomerAccount";
// import CustomerDetail from "./CustomerAccount/CustomerDetail.jsx";
import AcceptOrder from "./AcceptOrder";
import {UserProvider} from "../componentsDashboard/UserContext"; // Import UserProvider
import OrderDetail from "./OrderDetail/OrderDetail";
import OrderDetailForAccept from "./OrderDetailForAccept/OrderDetail";
import CreateBlog from "./CreateBlog.jsx";
import AcceptSupport from "./AcceptSupport.jsx";
import OrderDetailForSupport from "./OrderDetailForSupport/OrderDetail";

const Salepage = () => {
    return (
        <UserProvider>
            <div className="flex flex-col h-screen overflow-hidden">
                <div className="flex flex-grow overflow-hidden">
                    <div className="h-full bg-white shadow-lg">
                        <Sidebar/>
                    </div>

                    <div className="flex-grow h-full bg-gray-100">
                        <Routes>
                            <Route path="/" element={<AcceptOrder/>}/>
                            <Route path="/accept" element={<AcceptOrder/>}/>
                            <Route path="/allorder" element={<Order/>}/>
                            <Route path="/customeraccount" element={<CustomerAccount/>}/>
                            <Route
                                path="/allorder/detail/:orderId"
                                element={<OrderDetail/>}
                            />
                            <Route
                                path="/accept/detail/:orderId"
                                element={<OrderDetailForAccept/>}
                            />
                            <Route path="/createblog" element={<CreateBlog/>}/>

                            <Route path="/acceptsupport" element={<AcceptSupport/>}/>

                            <Route
                                path="/acceptsupport/detail/:orderId"
                                element={<OrderDetailForSupport/>}
                            />
                        </Routes>

                    </div>
                </div>
            </div>
        </UserProvider>
    );
};

export default Salepage;
