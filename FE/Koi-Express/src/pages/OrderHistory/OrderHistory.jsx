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
    setTempSelectedTimeFilter(selectedTimeFilter);
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
    { id: '#24PQ4AB1', pickup: '5/2 Phan Văn Trị, Phường 5, Quận Gò Vấp, Hồ Chí Minh', delivery: 'Cửa hàng tiện lợi Minh Tâm • 098 7654321, 200 Nguyễn Oanh, Phường 17, Quận Gò Vấp, Hồ Chí Minh', date: '2024-10-04T10:00', cod: 350000, fee: 15000, status: 'Hoàn thành' },
    { id: '#24PQ4KL5', pickup: '33 Lê Văn Lương, Phường Tân Kiểng, Quận 7, Hồ Chí Minh', delivery: 'BigC Quận 7 • 091 9876543, 99 Nguyễn Thị Thập, Phường Tân Phú, Quận 7, Hồ Chí Minh', date: '2024-10-03T08:30', cod: 700000, fee: 40000, status: 'Đang chờ' },
    { id: '#24PQ4ZY8', pickup: '25/8 Lê Hồng Phong, Phường 4, Quận 5, Hồ Chí Minh', delivery: 'Trường Tiểu học Vạn Phúc • 096 8765432, 120 Lý Thường Kiệt, Quận 10, Hồ Chí Minh', date: '2024-10-02T14:15', cod: 500000, fee: 30000, status: 'Chờ xác nhận' },
    { id: '#24P12LMN', pickup: '12/3 Cao Thắng, Phường 3, Quận 10, Hồ Chí Minh', delivery: 'Nhà thuốc Hòa Bình • 093 1122334, 45 Hoàng Văn Thụ, Phường 9, Quận Phú Nhuận, Hồ Chí Minh', date: '2024-09-29T16:40', cod: 250000, fee: 20000, status: 'Hoàn thành' },
    { id: '#24P9QWER', pickup: '65 Điện Biên Phủ, Phường 15, Quận Bình Thạnh, Hồ Chí Minh', delivery: 'Siêu thị CoopMart • 095 9874563, 456 Xô Viết Nghệ Tĩnh, Phường 25, Quận Bình Thạnh, Hồ Chí Minh', date: '2024-10-03T11:10', cod: 900000, fee: 35000, status: 'Đang chờ' },
    { id: '#24P9TYUI', pickup: '9 Đinh Bộ Lĩnh, Phường 24, Quận Bình Thạnh, Hồ Chí Minh', delivery: 'Bưu điện Phước Long • 094 9871234, 123 Đặng Văn Bi, Phường Bình Thọ, Thành phố Thủ Đức, Hồ Chí Minh', date: '2024-09-28T13:30', cod: 450000, fee: 25000, status: 'Đã hủy' },
    { id: '#24P0IUYT', pickup: '14 Nguyễn Hữu Cảnh, Phường 22, Quận Bình Thạnh, Hồ Chí Minh', delivery: 'Lotte Mart Quận 7 • 098 7650987, 469 Nguyễn Hữu Thọ, Phường Tân Hưng, Quận 7, Hồ Chí Minh', date: '2024-10-01T17:20', cod: 600000, fee: 40000, status: 'Hoàn thành' },
    { id: '#24P9ASDF', pickup: '17/8 Võ Văn Tần, Phường 6, Quận 3, Hồ Chí Minh', delivery: 'Nhà sách Nguyễn Văn Cừ • 097 6543210, 1053 Cách Mạng Tháng 8, Phường 7, Quận Tân Bình, Hồ Chí Minh', date: '2024-10-02T15:50', cod: 800000, fee: 30000, status: 'Chờ xác nhận' },
    { id: '#24PZXY12', pickup: '55 Phạm Ngọc Thạch, Phường 6, Quận 3, Hồ Chí Minh', delivery: 'Tiệm tạp hóa Minh Quang • 092 1234568, 20 Ngô Gia Tự, Phường 2, Quận 10, Hồ Chí Minh', date: '2024-10-04T11:35', cod: 1000000, fee: 35000, status: 'Đang chờ' },
    { id: '#24PQQWER', pickup: '87 Lý Tự Trọng, Phường Bến Thành, Quận 1, Hồ Chí Minh', delivery: 'Văn phòng Công ty XYZ • 090 4567891, 34 Nguyễn Huệ, Phường Bến Nghé, Quận 1, Hồ Chí Minh', date: '2024-09-30T08:50', cod: 1200000, fee: 50000, status: 'Hoàn thành' },
    { id: '#24P1OPLK', pickup: '32 Bàu Cát, Phường 14, Quận Tân Bình, Hồ Chí Minh', delivery: 'Cửa hàng bánh ABC • 091 1112233, 222 Trường Chinh, Phường 15, Quận Tân Bình, Hồ Chí Minh', date: '2024-10-03T09:40', cod: 400000, fee: 20000, status: 'Đã hủy' },
    { id: '#24PQWE23', pickup: '120 Võ Thị Sáu, Phường Tân Định, Quận 1, Hồ Chí Minh', delivery: 'Tiệm hoa Hương Quỳnh • 093 8765432, 30/5 Hai Bà Trưng, Phường 6, Quận 3, Hồ Chí Minh', date: '2024-10-01T10:30', cod: 1500000, fee: 45000, status: 'Chờ xác nhận' },
    { id: '#24P3ASDV', pickup: '21 Lê Duẩn, Phường Bến Nghé, Quận 1, Hồ Chí Minh', delivery: 'Công ty TNHH Đỉnh Cao • 095 5556789, 8 Nguyễn Huệ, Phường Bến Nghé, Quận 1, Hồ Chí Minh', date: '2024-10-03T14:05', cod: 200000, fee: 15000, status: 'Hoàn thành' },
    { id: '#24P3POIU', pickup: '43 Nguyễn Trãi, Phường Bến Thành, Quận 1, Hồ Chí Minh', delivery: 'Chung cư Green Land • 096 8881234, 10A Pasteur, Phường Bến Nghé, Quận 1, Hồ Chí Minh', date: '2024-09-28T12:45', cod: 700000, fee: 35000, status: 'Đang chờ' },
    { id: '#24PQPLMN', pickup: '102 Lê Thánh Tôn, Phường Bến Thành, Quận 1, Hồ Chí Minh', delivery: 'Văn phòng ACB Bank • 094 4447890, 77 Nguyễn Huệ, Phường Bến Nghé, Quận 1, Hồ Chí Minh', date: '2024-10-04T16:25', cod: 300000, fee: 20000, status: 'Chờ xác nhận' },
  ];

  const filterOrders = () => {
    let filteredOrders = orders;

    if (selectedTab !== 'Tất cả') {
      filteredOrders = filteredOrders.filter(order => order.status === selectedTab);
    }

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
        <div className="sticky top-0 z-20 bg-white">
          <div className="flex items-center justify-between mb-6">
            <h1 className="text-3xl font-bold text-gray-800">Lịch sử đơn hàng</h1>
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
            <input
              type="text"
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              placeholder="Tìm kiếm đơn hàng..."
              className="w-full max-w-md p-3 transition duration-300 border border-blue-300 rounded-lg shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
            />

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
                      </div>
                    </div>
                  )}
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
        </div>

        {/* Orders Table */}
        <div className="overflow-auto max-h-[60vh]">
          <table className="w-full text-left border-collapse shadow-md table-auto">
            <thead className="sticky top-0 z-10 bg-blue-100">
              <tr className="text-blue-900 border-b border-blue-200">
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
