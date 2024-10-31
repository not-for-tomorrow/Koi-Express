import React from "react";
import { Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, XAxis, YAxis, Tooltip } from "recharts";

const data = [
    { name: 'Jan', Expenses: 4000, Income: 2400 },
    { name: 'Feb', Expenses: 3000, Income: 1398 },
    { name: 'Apr', Expenses: 2700, Income: 3908 },
    { name: 'May', Expenses: 1890, Income: 4800 },
    { name: 'Jun', Expenses: 2390, Income: 3800 },
    { name: 'July', Expenses: 3490, Income: 4300 },
    { name: 'Aug', Expenses: 2000, Income: 9800 },
    { name: 'Oct', Expenses: 32000, Income: 6800 },
    { name: 'Nov', Expenses: 2500, Income: 4300 },
    { name: 'Dec', Expenses: 3000, Income: 1398 }
];

function TransactionChart() {
    return (
        <div className="w-[20rem] h-[22rem] bg-white p-4 rounded-sm border border-gray-200 flex flex-col flex-1">
            <strong className="text-gray-700 font-medium">Transaction</strong>
            <div className="w-full mt-3 flex-1 text-xs">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart
                        width={500}
                        height={500}
                        data={data}
                        margin={{ top: 20, right: 30, left: 0, bottom: 0 }}
                    >
                        <CartesianGrid strokeDasharray="3 3" vertical={false} />
                        <XAxis dataKey="name" />
                        <YAxis label={{ value: "Amount ($)", angle: -90, position: 'insideLeft' }} />
                        <Tooltip />
                        <Legend />
                        <Bar dataKey="Expenses" fill="#ff7300" />
                        <Bar dataKey="Income" fill="#387908" />
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}

export default TransactionChart;
