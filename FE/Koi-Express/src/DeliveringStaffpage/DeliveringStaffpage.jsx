import React from "react";
import {Route, Routes} from "react-router-dom";
import Sidebar from "../componentsDashboard/DeliveringStaffcomponents/Sidebar/Sidebar";
import Order from "./Order";
import {UserProvider} from "../componentsDashboard/UserContext";
import OrderDetail from "./OrderDetail/OrderDetail";
import DeliverOrder from "./DeliverOrder/DeliverOrder";
import PaymentSuccessful from "./PaymentSuccessful";
import PaymentFailed from "./PaymentFailed";

const DeliveringStaffpage = () => {
    return (
        <UserProvider>
            <div className="flex flex-col h-screen overflow-hidden">
                <div className="flex flex-grow overflow-hidden">
                    <div className="h-full bg-white shadow-lg">
                        <Sidebar/>
                    </div>

                    <div className="flex-grow h-full bg-gray-100">
                        <Routes>
                            <Route path="/" element={<Order/>}/>
                            <Route path="/Order" element={<Order/>}/>
                            <Route path="/Order/detail/:orderId" element={<OrderDetail/>}/>
                            <Route path="/deliverorder" element={<DeliverOrder/>}/>
                            <Route path="/payment-successful" element={<PaymentSuccessful/>}/>
                            <Route path="/payment-failed" element={<PaymentFailed/>}/>
                        </Routes>
                    </div>
                </div>
            </div>
        </UserProvider>
    );
};

export default DeliveringStaffpage;
