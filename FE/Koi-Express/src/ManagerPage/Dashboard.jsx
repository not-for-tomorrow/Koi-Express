import React from 'react';
import DashboardStartGrid from "/src/ManagerPage/DashboardStartGrid.jsx";
import TransactionChart from "/src/ManagerPage/TransactionChart.jsx";
import BuyerProfileChart from "./BuyerProfileChart.jsx";
import RecentOrders from "./RecentOrders.jsx";
import TopCustomers from "./TopCustomers.jsx";

export default function Dashboard() {
    return(
        <div className='flex flex-col gap-4'>
            <DashboardStartGrid/>

            <div className="flex flex-row gap-4 w-full">
                <TransactionChart/>
                <BuyerProfileChart/>
            </div>

            <div className="flex flex-row gap-4 w-full">
                <RecentOrders />
                <TopCustomers />
            </div>

        </div>
    )
}