import React from "react";
import {Link} from "react-router-dom";

const TopCustomersData = [
    {
        id: '1',
        name: 'Ntt'
    },
    {
        id: '2',
        name: 'Ntt'
    },
    {
        id: '3',
        name: 'Ntt'
    },
    {
        id: '4',
        name: 'Ntt'
    },
    {
        id: '5',
        name: 'Ntt'
    },
]

function TopCustomers() {
    return(
        <div className="bg-white px-4 pt-3 pb-4 rounded-sm border border-gray-200 w-[20rem]" >
            <strong className="text-gray-700 font-medium">Top Customers</strong>
            <div className="mt-4 flex flex-col gap-3">
                {TopCustomersData.map(customeraccount => (
                    <Link to={`/customeraccount/${customeraccount.id}`}>
                        <div className="ml-4 flex-1">
                            <p className="text-sm text-gray-800">
                                {customeraccount.id}
                            </p>
                        </div>

                        <div className="text-xs">
                            {customeraccount.name}
                        </div>
                    </Link>
                ))}
            </div>
        </div>
    )
}

export default TopCustomers;