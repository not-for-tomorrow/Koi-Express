export const getDateRangeDisplay = (filter, customDateRange) => {
    const now = new Date();
    let fromDate, toDate;

    switch (filter) {
        case "today":
            fromDate = toDate = now.toLocaleDateString("vi-VN");
            return `Ngày: ${fromDate}`;
        case "this-week":
            const startOfWeek = new Date(now);
            startOfWeek.setDate(now.getDate() - now.getDay());
            const endOfWeek = new Date(now);
            endOfWeek.setDate(now.getDate() + (6 - now.getDay()));
            fromDate = startOfWeek.toLocaleDateString("vi-VN");
            toDate = endOfWeek.toLocaleDateString("vi-VN");
            return `Tuần này: ${fromDate} - ${toDate}`;
        case "this-month":
            const startOfMonth = new Date(now.getFullYear(), now.getMonth(), 1);
            const endOfMonth = new Date(now.getFullYear(), now.getMonth() + 1, 0);
            fromDate = startOfMonth.toLocaleDateString("vi-VN");
            toDate = endOfMonth.toLocaleDateString("vi-VN");
            return `Tháng này: ${fromDate} - ${toDate}`;
        case "custom":
            if (customDateRange.from && customDateRange.to) {
                fromDate = new Date(customDateRange.from).toLocaleDateString("vi-VN");
                toDate = new Date(customDateRange.to).toLocaleDateString("vi-VN");
                return `Tùy chỉnh: ${fromDate} - ${toDate}`;
            }
            break;
        default:
            return "Tất cả";
    }
};
