import { useEffect, useState } from "react";
import API from "../../api/axios";
import "./admin.css";

const AdminOrders = () => {
  const [orders, setOrders] = useState([]);
  const [updatingId, setUpdatingId] = useState(null);

  const fetchOrders = async () => {
    try {
      const res = await API.get("/orders/admin/all");
      setOrders(res.data);
    } catch (err) {
      console.error("Error fetching orders:", err);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleStatusChange = async (orderId, newStatus) => {
    try {
      setUpdatingId(orderId);

      await API.put(
        `/orders/admin/update-status/${orderId}?status=${newStatus}`
      );

      await fetchOrders();
    } catch (err) {
      console.error("Status update failed:", err);
      alert("Failed to update status");
    } finally {
      setUpdatingId(null);
    }
  };

  return (
    <div>
      <h2>All Orders</h2>

      {orders.length === 0 ? (
        <p>No orders available</p>
      ) : (
        orders.map((order) => (
          <div key={order.orderId} className="admin-order-card">

            {/* HEADER */}
            <div className="admin-order-header">
              <h3>Order #{order.orderId}</h3>
              <span className={`status-badge ${order.status.toLowerCase()}`}>
                {order.status}
              </span>
            </div>

            {/* CUSTOMER DETAILS */}
            <p><strong>Customer:</strong> {order.customerName}</p>
            <p><strong>Phone:</strong> {order.phoneNumber}</p>
            <p>
              <strong>Address:</strong>{" "}
              {order.address}, {order.city} - {order.pincode}
            </p>

            <p><strong>Total:</strong> ₹{order.totalAmount}</p>
            <p>
              <strong>Date:</strong>{" "}
              {new Date(order.orderDate).toLocaleString()}
            </p>

            {/* ITEMS */}
            <div className="admin-items">
              <h4>Items:</h4>
              {order.items.map((item, index) => (
                <div
                  key={`${order.orderId}-${item.productId}-${index}`}
                  className="admin-item-row"
                >
                  {item.productName} × {item.quantity} — ₹{item.subtotal}
                </div>
              ))}
            </div>

            {/* STATUS UPDATE */}
            <div className="admin-status-update">
              <select
                value={order.status}
                disabled={updatingId === order.orderId}
                onChange={(e) =>
                  handleStatusChange(order.orderId, e.target.value)
                }
              >
                <option value="PLACED">PLACED</option>
                <option value="SHIPPED">SHIPPED</option>
                <option value="DELIVERED">DELIVERED</option>
                <option value="CANCELLED">CANCELLED</option>
              </select>
            </div>

          </div>
        ))
      )}
    </div>
  );
};

export default AdminOrders;
