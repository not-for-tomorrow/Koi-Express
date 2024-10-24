import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Sidebar from '../../componentsDashboard/DeliveringStaffcomponents/Sidebar/Sidebar'
import Order from './Order'

const DeliveringStaffpage = () => {
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
        </Routes>
      </div>
    </div>
  )
}

export default DeliveringStaffpage