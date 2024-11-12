import HomeIcon from '@mui/icons-material/Home';
import HistoryIcon from '@mui/icons-material/History';

export const navItems = [
    {
        id: 1,
        icons: (
            <HomeIcon
                style={{fontSize: 25, color: "#1976d2"}}
                className="min-w-[35px] min-h-[35px]"
            />
        ),
        title: "Đơn hàng mới",
    },
    {
        id: 2,
        icons: (
            <HistoryIcon
                style={{fontSize: 25, color: "#f57c00"}}
                className="min-w-[35px] min-h-[35px]"
            />
        ),
        title: "Lịch sử đơn hàng",
    },

];
