import React, {useEffect, useState} from 'react';
import axios from 'axios';
import {IoBagHandle} from 'react-icons/io5';

function DashboardStartGrid() {
    const [totalAmountDaily, setTotalAmountDaily] = useState(null);
    const [totalAmount, setTotalAmount] = useState(null);
    const [numberOfCustomers, setNumberOfCustomers] = useState(null);
    const [numberOfOrders, setNumberOfOrders] = useState(null);

    useEffect(() => {
        // Fetch data from each API endpoint
        const fetchData = async () => {
            try {
                const token = localStorage.getItem('token');
                const headers = {
                    Authorization: `Bearer ${token}`,
                };

                const dailyAmountResponse = await axios.get('http://localhost:8080/api/manager/total-amount/daily', {headers});
                setTotalAmountDaily(dailyAmountResponse.data.result);

                const totalAmountResponse = await axios.get('http://localhost:8080/api/manager/total-amount', {headers});
                setTotalAmount(totalAmountResponse.data.result);

                const customersResponse = await axios.get('http://localhost:8080/api/manager/number-of-customers', {headers});
                setNumberOfCustomers(customersResponse.data.result);

                const ordersResponse = await axios.get('http://localhost:8080/api/manager/number-of-orders', {headers});
                setNumberOfOrders(ordersResponse.data.result);
            } catch (error) {
                console.error("Error fetching data", error);
            }
        };


        fetchData();
    }, []);

    const stats = [
        {
            color: 'bg-sky-500',
            label: 'Total Amount / Day',
            amount: totalAmountDaily !== null ? `$${totalAmountDaily}` : 'Loading...',
        },
        {
            color: 'bg-amber-700',
            label: 'Total Amount',
            amount: totalAmount !== null ? `$${totalAmount}` : 'Loading...',
        },
        {
            color: 'bg-amber-400',
            label: 'Number of Customers',
            amount: numberOfCustomers !== null ? numberOfCustomers : 'Loading...',
        },
        {
            color: 'bg-emerald-800',
            label: 'Number of Orders',
            amount: numberOfOrders !== null ? numberOfOrders : 'Loading...',
        },
    ];

    return (
        <div className="flex gap-4 w-full">
            {stats.map((stat, index) => (
                <BoxWrapper key={index}>
                    <div className={`rounded-full h-12 w-12 flex items-center justify-center ${stat.color}`}>
                        <IoBagHandle className="text-2xl text-white"/>
                    </div>
                    <div className="pl-4">
                        <span className="text-sm text-gray-500 font-light">{stat.label}</span>
                        <div className="flex items-center">
                            <strong className="text-xl text-gray-700 font-semibold">{stat.amount}</strong>
                        </div>
                    </div>
                </BoxWrapper>
            ))}
        </div>
    );
}

export default DashboardStartGrid;

function BoxWrapper({children}) {
    return (
        <div className="bg-white rounded-lg p-4 flex-1 border-2 border-gray-400 shadow-sm flex items-center">
            {children}
        </div>
    );
}


