import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Sidebar from '../../componentsDashboard/Salepage/Sidebar/Sidebar'
import Order from '../../componentsDashboard/Salepage/pages/Order/Order'
import CustomerAccount from '../../componentsDashboard/Salepage/pages/CustomerAccount/CustomerAccount'
import AcceptOrder from '../../componentsDashboard/Salepage/pages/AcceptOrder/AcceptOrder'

const Salepage = () => {
  return (
    <div className="flex h-screen">
      {/* Sidebar */}
      <div className="transition-all duration-300 bg-white shadow-lg">
        <Sidebar />
      </div>

      {/* Content area */}
      <div className="flex-grow h-full overflow-auto bg-gray-100">
        <Routes>
          <Route path="/" element={<Order />} />
          <Route path="/customeraccount" element={<CustomerAccount />} />
          <Route path="/acceptorder" element={<AcceptOrder />} />
        </Routes>
      </div>
    </div>
  )
}

export default Salepage