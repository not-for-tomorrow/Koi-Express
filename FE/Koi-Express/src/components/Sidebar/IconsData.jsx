import HomeIcon from '@mui/icons-material/Home';
import HistoryIcon from '@mui/icons-material/History';
import AnnouncementIcon from '@mui/icons-material/Announcement';
import HelpOutlineIcon from '@mui/icons-material/HelpOutline';

export const navItems = [
  {
    id: 1,
    icons: (
        <HomeIcon
            style={{ fontSize: 35, color: "#1976d2" }}
            className="min-w-[35px] min-h-[35px]"
        />
    ),
    title: "Đơn hàng mới",
  },
  {
    id: 2,
    icons: (
        <HistoryIcon
            style={{ fontSize: 35, color: "#f57c00" }}
            className="min-w-[35px] min-h-[35px]"
        />
    ),
    title: "Lịch sử đơn hàng",
  },
  {
    id: 3,
    icons: (
        <AnnouncementIcon
            style={{ fontSize: 35, color: "#d32f2f" }}
            className="min-w-[35px] min-h-[35px]"
        />
    ),
    title: "Tin mới",
  },
  {
    id: 4,
    icons: (
        <HelpOutlineIcon
            style={{ fontSize: 35, color: "#388e3c" }}
            className="min-w-[35px] min-h-[35px]"
        />
    ),
    title: "Trợ giúp",
  },
];
