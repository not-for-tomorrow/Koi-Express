import React, {useEffect, useState} from "react";
import {Bar, BarChart, CartesianGrid, Legend, ResponsiveContainer, XAxis, YAxis, Tooltip} from "recharts";
import axios from "axios";

function TransactionChart() {
    const [data, setData] = useState([]);
    const [year, setYear] = useState(new Date().getFullYear());

    useEffect(() => {
        const fetchMonthlyTotals = async () => {
            const token = localStorage.getItem("token");
            if (!token) {
                console.error("Authorization token is missing.");
                return;
            }

            try {
                const response = await axios.get(`http://localhost:8080/api/manager/total-amount/yearly`, {
                    params: {year},
                    headers: {
                        Authorization: `Bearer ${token}`,
                    },
                });

                if (response.status === 200 && response.data.result) {
                    const chartData = Object.keys(response.data.result).map((monthKey) => {
                        const monthNumber = parseInt(monthKey.split("-")[1], 10);
                        return {
                            month: monthNumber,
                            Total: response.data.result[monthKey]
                        };
                    });
                    setData(chartData);
                } else {
                    console.error("Failed to fetch monthly totals:", response.data.message || "Unexpected response format.");
                }
            } catch (error) {
                if (error.response && error.response.status === 401) {
                    console.error("Unauthorized: Invalid or expired token. Please log in again.");
                } else {
                    console.error("Error fetching monthly totals:", error.message || error);
                }
            }
        };

        fetchMonthlyTotals();
    }, [year]);

    const handleYearChange = (event) => {
        setYear(parseInt(event.target.value, 10));
    };

    return (
        <div className="w-[58rem] max-w-5xl mx-auto bg-white p-8 rounded-lg border-2 border-gray-400 shadow-md">
            <h2 className="text-2xl font-semibold text-gray-800 text-center mb-6">
                Tóm tắt giao dịch cho {year}
            </h2>
            <div className="flex items-center justify-center mb-6">
                <label htmlFor="year-select" className="text-sm text-gray-700 mr-2">Chọn Năm:</label>
                <select
                    id="year-select"
                    value={year}
                    onChange={handleYearChange}
                    className="border-2 border-gray-400 rounded p-2 text-gray-700 focus:border-blue-500 focus:outline-none"
                >
                    {[...Array(5)].map((_, i) => {
                        const optionYear = new Date().getFullYear() - i;
                        return (
                            <option key={optionYear} value={optionYear}>
                                {optionYear}
                            </option>
                        );
                    })}
                </select>
            </div>
            <div className="w-full h-96">
                <ResponsiveContainer width="100%" height="100%">
                    <BarChart data={data} margin={{top: 20, right: 30, left: 20, bottom: 20}}>
                        <CartesianGrid strokeDasharray="3 3" vertical={false} stroke="#ddd"/>
                        <XAxis
                            dataKey="month"
                            label={{value: "Month", position: 'insideBottomRight', offset: -10}}
                            tick={{fontSize: 12, fill: '#888'}}
                        />
                        <YAxis
                            label={{value: "Total Amount ($)", angle: -90, position: 'insideLeft', offset: -14}}
                            tick={{fontSize: 12, fill: '#888'}}
                            domain={[0, 'dataMax']}
                        />
                        <Tooltip
                            cursor={{fill: '#f0f0f0'}}
                            contentStyle={{backgroundColor: '#fff', border: '1px solid #ccc', borderRadius: '8px'}}
                        />
                        <Legend/>
                        <Bar dataKey="Total" fill="#007BFF" stroke="#004080" strokeWidth={2} barSize={30}
                             radius={[4, 4, 0, 0]}/>
                    </BarChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}

export default TransactionChart;
