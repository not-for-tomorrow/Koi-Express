import React from 'react';
import DashboardStartGrid from "/src/ManagerPage/DashboardStartGrid.jsx";
import TransactionChart from "/src/ManagerPage/TransactionChart.jsx";
import TopCustomers from "./TopCustomers.jsx";

export default function Dashboard() {
    return (
        <div className='flex flex-col gap-4'>
            <DashboardStartGrid className="mt-1" style={{marginTop: '5px'}}/>

            <div className="flex flex-row gap-4 w-full">
                <TransactionChart/>
                <TopCustomers/>
            </div>
        </div>
    )
}
