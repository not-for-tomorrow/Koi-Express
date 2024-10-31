import React from "react";
import {Link} from "react-router-dom";

const RecentOrderData = [
    {
        id : '1',
        product_id: '4324',
        customer_name: 'NTT',
        order_date: '2024-12-12',
        order_total: '$435.50',
        current_order_status: 'Placed',
        shipment_address: 'Cottage Grove, OR 97424 '
    },

    {
        id : '2',
        product_id: '5324',
        customer_name: 'NTT',
        order_date: '2024-12-12',
        order_total: '$435.50',
        current_order_status: 'Placed',
        shipment_address: 'Cottage Grove, OR 97424 '
    },

    {
        id : '3',
        product_id: '9324',
        customer_name: 'NTT',
        order_date: '2024-12-12',
        order_total: '$435.50',
        current_order_status: 'Placed',
        shipment_address: 'Cottage Grove, OR 97424 '
    },

    {
        id : '4',
        product_id: '4324',
        customer_name: 'NTT',
        order_date: '2024-12-12',
        order_total: '$435.50',
        current_order_status: 'Placed',
        shipment_address: 'Cottage Grove, OR 97424 '
    },

    {
        id : '5',
        product_id: '4324',
        customer_name: 'NTT',
        order_date: '2024-12-12',
        order_total: '$435.50',
        current_order_status: 'Placed',
        shipment_address: 'Cottage Grove, OR 97424 '
    },
]

function RecentOrders() {
    return(
        <div className="bg-white px-4 pt-3 pb-4 rounded-sm border border-gray-200 flex-1" >
            <strong className="text-gray-700 font-medium"> Recent Orders</strong>
            <div className="mt-3">
                <table className="w-full text-gray-700">
                    <thead>
                        <tr>
                            <td>ID</td>
                            <td>Product Id</td>
                            <td>Customer Name</td>
                            <td>Order Date</td>
                            <td>Order Total</td>
                            <td>Shipping Address</td>
                            <td>Order Status</td>
                        </tr>
                    </thead>

                    <tbody>
                        {RecentOrderData.map((order) => (
                            <tr key={order.id}>
                                <td>
                                    <Link to={'/order/$order.id'}>{order.id}</Link>
                                </td>
                                <td>
                                    <Link to={'/order/$order.product_id'}>{order.product_id}</Link>
                                </td>
                                <td><Link>{order.customer_name}</Link></td>
                                <td>{new Date(order.order_date).toLocaleDateString()}</td>
                                <td>{order.order_total}</td>
                                <td>{order.shipment_address}</td>
                                <td>{order.current_order_status}</td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    )
}

export default RecentOrders;