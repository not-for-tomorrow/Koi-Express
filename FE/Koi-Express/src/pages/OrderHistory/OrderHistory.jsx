import React, { useState } from 'react';

const OrderHistory = () => {
  const [isTimeFilterExpanded, setIsTimeFilterExpanded] = useState(false);
  const [selectedTimeFilter, setSelectedTimeFilter] = useState('all');
  const [tempSelectedTimeFilter, setTempSelectedTimeFilter] = useState('all');
  const [customDateRange, setCustomDateRange] = useState({ from: '', to: '' });
  const [displayDateRange, setDisplayDateRange] = useState('');
  const [selectedTab, setSelectedTab] = useState('Tất cả');
  const [searchQuery, setSearchQuery] = useState('');

  const handleTimeFilterClick = () => {
    setTempSelectedTimeFilter(selectedTimeFilter); // Save current filter before expanding
    setIsTimeFilterExpanded(!isTimeFilterExpanded);
  };

  const handleTimeFilterSelect = (filter) => {
    setTempSelectedTimeFilter(filter);
    if (filter !== 'custom') {
      setCustomDateRange({ from: '', to: '' });
    }
  };

  const handleApplyFilter = () => {
    setSelectedTimeFilter(tempSelectedTimeFilter);
    setIsTimeFilterExpanded(false);

    // Set the date range display based on the selected filter
    const now = new Date();
    let fromDate, toDate;
    switch (tempSelectedTimeFilter) {
      case 'today':
        fromDate = toDate = now.toLocaleDateString('vi-VN');
        setDisplayDateRange(`Ngày: ${fromDate}`);
        break;
      case 'this-week':
        const startOfWeek = new Date(now);
        startOfWeek.setDate(now.getDate() - now.getDay());
        const endOfWeek = new Date(now);
        endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
        fromDate = startOfWeek.toLocaleDateString('vi-VN');
        toDate = endOfWeek.toLocaleDateString('vi-VN');
        setDisplayDateRange(`Tuần này: ${fromDate} - ${toDate}`);
        break;
      case 'this-month':
        const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
        const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
        fromDate = startOfMonth.toLocaleDateString('vi-VN');
        toDate = endOfMonth.toLocaleDateString('vi-VN');
        setDisplayDateRange(`Tháng này: ${fromDate} - ${toDate}`);
        break;
      case 'custom':
        if (customDateRange.from && customDateRange.to) {
          fromDate = new Date(customDateRange.from).toLocaleDateString('vi-VN');
          toDate = new Date(customDateRange.to).toLocaleDateString('vi-VN');
          setDisplayDateRange(`Tùy chỉnh: ${fromDate} - ${toDate}`);
        }
        break;
      default:
        setDisplayDateRange('Tất cả');
        break;
    }
  };

  const handleCloseFilter = () => {
    setIsTimeFilterExpanded(false);
  };

  const handleCustomDateChange = (field, value) => {
    setCustomDateRange({ ...customDateRange, [field]: value });
  };

  const orders = [
    { id: '#24PQ4KL2', pickup: '30/6 đường 100 Bình Thới, Phường 14, Quận 11, Hồ Chí Minh', delivery: 'Aeonmall Bình Tân • 090 9873564, 138 Tên lửa, Phường Tân Ca, Quận Bình Tân, Hồ Chí Minh', date: '2024-10-03T15:52', cod: 600000, fee: 25000, status: 'Hoàn thành' },
    { id: '#24P22XTS', pickup: '45/3 Tạ Uyên, Phường 12, Quận 05, Hồ Chí Minh', delivery: 'Ngân hàng VIB • 099 1453752, 16 Tân Hương, Phường Tân Thành, Quận Tân Phú, Hồ Chí Minh', date: '2024-10-01T15:50', cod: 600000, fee: 35000, status: 'Hoàn thành' },
    { id: '#24PQA672', pickup: '12 Nguyễn Văn Cừ, Phường An Hòa, Quận Ninh Kiều, Cần Thơ', delivery: 'Shop điện tử Phương Nam • 093 1234567, 24 Hùng Vương, Phường 2, Quận 5, Hồ Chí Minh', date: '2024-09-28T14:25', cod: 800000, fee: 30000, status: 'Đang chờ' },
    { id: '#24P3L6TF', pickup: '85 Nguyễn Đình Chiểu, Phường 6, Quận 3, Hồ Chí Minh', delivery: 'Nguyễn Kim • 094 7890123, 300 Lý Thường Kiệt, Quận 10, Hồ Chí Minh', date: '2024-10-02T12:00', cod: 1200000, fee: 50000, status: 'Chờ xác nhận' },
    { id: '#24P9UIOP', pickup: '123 Pasteur, Phường Bến Nghé, Quận 1, Hồ Chí Minh', delivery: 'Bách Hóa Xanh • 097 3456789, 12 Trường Chinh, Quận Tân Bình, Hồ Chí Minh', date: '2024-09-30T09:45', cod: 500000, fee: 20000, status: 'Đã hủy' },
    // More orders can be added here for more extensive testing
  ];

  // Function to filter orders based on selected filters
  const filterOrders = () => {
    let filteredOrders = orders;

    // Filter by selected tab
    if (selectedTab !== 'Tất cả') {
      filteredOrders = filteredOrders.filter(order => order.status === selectedTab);
    }

    // Filter by date range
    if (selectedTimeFilter !== 'all') {
      filteredOrders = filteredOrders.filter(order => {
        const orderDate = new Date(order.date);
        if (selectedTimeFilter === 'today') {
          const today = new Date();
          return orderDate.toDateString() === today.toDateString();
        } else if (selectedTimeFilter === 'this-week') {
          const now = new Date();
          const startOfWeek = new Date(now);
          startOfWeek.setDate(now.getDate() - now.getDay());
          const endOfWeek = new Date(now);
          endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
          return orderDate >= startOfWeek && orderDate <= endOfWeek;
        } else if (selectedTimeFilter === 'this-month') {
          const now = new Date();
          const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
          const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
          return orderDate >= startOfMonth && orderDate <= endOfMonth;
        } else if (selectedTimeFilter === 'custom') {
          if (customDateRange.from && customDateRange.to) {
            const from = new Date(customDateRange.from);
            const to = new Date(customDateRange.to);
            return orderDate >= from && orderDate <= to;
          }
          return false;
        }
        return true;
      });
    }

    // Filter by search query
    if (searchQuery) {
      filteredOrders = filteredOrders.filter(order => {
        return (
          order.id.includes(searchQuery) ||
          order.pickup.toLowerCase().includes(searchQuery.toLowerCase()) ||
          order.delivery.toLowerCase().includes(searchQuery.toLowerCase())
        );
      });
    }

    return filteredOrders;
  };

  return (
    <div className="min-h-screen p-8 bg-gradient-to-r from-blue-100 to-blue-50">
      <div className="p-8 bg-white rounded-lg shadow-lg">
        {/* Header Section */}
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-3xl font-bold text-gray-800">Lịch sử đơn hàng</h1>
          <button className="flex items-center p-3 text-white transition duration-300 rounded-lg bg-gradient-to-r from-blue-500 to-blue-600 hover:shadow-lg">
            <span className="mr-2 font-medium">Xuất dữ liệu</span>
            <img
              src="/export-icon.png"
              alt="Export Icon"
              className="w-5 h-5"
            />
          </button>
        </div>

        {/* Tabs Section */}
        <div className="flex mb-6 space-x-4 overflow-x-auto">
          {["Tất cả", "Chờ xác nhận", "Chờ thanh toán", "Đang chờ", "Đang giao", "Hoàn thành", "Đã hủy"].map((tab, index) => (
            <button
              key={index}
              onClick={() => setSelectedTab(tab)}
              className={`px-5 py-2 rounded-full transition duration-300 ${
                selectedTab === tab
                  ? "bg-blue-500 text-white shadow-md"
                  : "bg-blue-100 text-blue-700 hover:bg-blue-200"
              }`}
            >
              {tab}
            </button>
          ))}
        </div>

        {/* Search and Filter Section */}
        <div className="flex items-center mb-6 space-x-6">
          {/* Search Box */}
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Tìm kiếm đơn hàng..."
            className="w-full max-w-md p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />

          {/* Time Filter */}
          <label className="font-medium text-gray-600">Thời gian:</label>
          <div className="relative">
            <button
              onClick={handleTimeFilterClick}
              className="flex items-center p-3 text-blue-700 transition duration-300 bg-blue-100 rounded-lg shadow-sm hover:bg-blue-200"
            >
              <span>{displayDateRange || 'Tất cả'}</span>
            </button>
            {isTimeFilterExpanded && (
              <div className="absolute left-0 w-64 p-4 mt-2 bg-white border border-blue-300 rounded-lg shadow-lg">
                {['all', 'today', 'this-week', 'this-month', 'custom'].map((filter) => (
                  <div key={filter} className="mb-2">
                    <label className="flex items-center">
                      <input
                        type="radio"
                        name="timeFilter"
                        value={filter}
                        checked={tempSelectedTimeFilter === filter}
                        onChange={() => handleTimeFilterSelect(filter)}
                        className="mr-2"
                      />
                      {filter === 'all' ? 'Tất cả' : filter === 'today' ? 'Hôm nay' : filter === 'this-week' ? 'Tuần này' : filter === 'this-month' ? 'Tháng này' : 'Tùy chỉnh'}
                    </label>
                  </div>
                ))}
                {tempSelectedTimeFilter === 'custom' && (
                  <div className="flex flex-col mt-4 space-y-4">
                    <div className="relative">
                      <label className="block mb-1 font-medium text-gray-600">Từ ngày:</label>
                      <input
                        type="date"
                        value={customDateRange.from}
                        max={new Date().toISOString().split('T')[0]}
                        onChange={(e) => handleCustomDateChange('from', e.target.value)}
                        className="w-full p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                      <span className="absolute transform -translate-y-1/2 top-1/2 right-3">
                        <img src="/calendar-icon.png" alt="Calendar Icon" className="w-5 h-5" />
                      </span>
                    </div>
                    <div className="relative">
                      <label className="block mb-1 font-medium text-gray-600">Đến ngày:</label>
                      <input
                        type="date"
                        value={customDateRange.to}
                        min={customDateRange.from}
                        max={new Date().toISOString().split('T')[0]}
                        onChange={(e) => handleCustomDateChange('to', e.target.value)}
                        className="w-full p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
                      />
                      <span className="absolute transform -translate-y-1/2 top-1/2 right-3">
                        <img src="/calendar-icon.png" alt="Calendar Icon" className="w-5 h-5" />
                      </span>
                    </div>
                  </div>
                )}
                {/* Apply and Close Buttons */}
                <div className="flex justify-end mt-4 space-x-3">
                  <button
                    onClick={handleCloseFilter}
                    className="px-4 py-2 text-sm font-semibold text-gray-700 transition duration-300 bg-gray-100 rounded-lg hover:bg-gray-200"
                  >
                    Đóng
                  </button>
                  <button
                    onClick={handleApplyFilter}
                    className="px-4 py-2 text-sm font-semibold text-white transition duration-300 bg-blue-500 rounded-lg hover:bg-blue-600"
                  >
                    Áp dụng
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>

        {/* Orders Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse shadow-md table-auto">
            <thead>
              <tr className="text-blue-900 bg-blue-100 border-b border-blue-200">
                <th className="p-4 font-semibold">Mã đơn hàng</th>
                <th className="p-4 font-semibold">Điểm lấy hàng</th>
                <th className="p-4 font-semibold">Điểm giao hàng</th>
                <th className="p-4 font-semibold">Thời gian tạo</th>
                <th className="p-4 font-semibold">Tổng COD</th>
                <th className="p-4 font-semibold">Tổng phí</th>
                <th className="p-4 font-semibold">Trạng thái</th>
              </tr>
            </thead>
            <tbody>
              {filterOrders().map((order, index) => (
                <tr key={index} className="transition duration-300 border-b border-gray-200 hover:bg-blue-50">
                  <td className="p-4 font-semibold text-blue-600">{order.id}</td>
                  <td className="p-4 text-sm text-gray-700">{order.pickup}</td>
                  <td className="p-4 text-sm text-gray-700">{order.delivery}</td>
                  <td className="p-4 text-sm text-gray-700">{new Date(order.date).toLocaleString('vi-VN')}</td>
                  <td className="p-4 text-sm font-medium text-blue-600">đ {order.cod.toLocaleString('vi-VN')}</td>
                  <td className="p-4 text-sm text-gray-700">đ {order.fee.toLocaleString('vi-VN')}</td>
                  <td className="p-4">
                    <span
                      className={`inline-block px-3 py-1 rounded-full text-sm font-semibold ${
                        order.status === 'Hoàn thành'
                          ? 'bg-blue-100 text-blue-600'
                          : order.status === 'Đã hủy'
                          ? 'bg-red-100 text-red-600'
                          : 'bg-yellow-100 text-yellow-600'
                      }`}
                    >
                      {order.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default OrderHistory;
