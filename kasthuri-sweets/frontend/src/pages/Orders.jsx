import { useEffect, useState } from "react";
import API from "../api/axios";

const Orders = () => {
  const [orders, setOrders] = useState([]);
  const [expandedOrders, setExpandedOrders] = useState([]); // ✅ multiple expand
  const [message, setMessage] = useState("");

  const fetchOrders = async () => {
    try {
      const res = await API.get("/orders/my");
      setOrders(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  // ✅ Toggle multiple
  const toggleDetails = (orderId) => {
    if (expandedOrders.includes(orderId)) {
      setExpandedOrders(
        expandedOrders.filter((id) => id !== orderId)
      );
    } else {
      setExpandedOrders([...expandedOrders, orderId]);
    }
  };

  const handleCancel = async (orderId) => {
    try {
      await API.put(`/orders/cancel/${orderId}`);
      setMessage("Order cancelled successfully.");
      fetchOrders();
      setTimeout(() => setMessage(""), 2000);
    } catch (error) {
      console.log(error);
      setMessage("Unable to cancel order.");
      setTimeout(() => setMessage(""), 2000);
    }
  };

  return (
    <div>
      <h2>My Orders</h2>

      {message && (
        <p style={{ color: "green", marginBottom: "10px" }}>
          {message}
        </p>
      )}

      {orders.length === 0 ? (
        <p>No orders placed yet.</p>
      ) : (
        <div className="products-grid">
          {orders.map((order) => (
            <div key={order.orderId} className="product-card">
              <h3>Order #{order.orderId}</h3>

              <p>
                <strong>Status:</strong>{" "}
                <span
                  style={{
                    color:
                      order.status === "DELIVERED"
                        ? "green"
                        : order.status === "CANCELLED"
                        ? "red"
                        : "#2563eb",
                  }}
                >
                  {order.status}
                </span>
              </p>

              <p className="price">
                Total: ₹{order.totalAmount}
              </p>

              <p>
                Placed on:{" "}
                {new Date(order.orderDate).toLocaleDateString()}
              </p>

              {/* View / Hide Button */}
              <button
                className="btn-primary"
                onClick={() => toggleDetails(order.orderId)}
              >
                {expandedOrders.includes(order.orderId)
                  ? "Hide Details"
                  : "View Details"}
              </button>

              {/* Expand Section */}
              {expandedOrders.includes(order.orderId) && (
                <div style={{ marginTop: "10px" }}>
                  <h4>Items:</h4>
                  {order.items.map((item) => (
                    <p key={item.productId}>
                      {item.productName} × {item.quantity} — ₹{item.subtotal}
                    </p>
                  ))}

                  <h4>Delivery Address:</h4>
                  <p>
                    {order.address}, {order.city} - {order.pincode}
                  </p>

                  {order.status === "PLACED" && (
                    <button
                      className="btn-danger"
                      onClick={() =>
                        handleCancel(order.orderId)
                      }
                    >
                      Cancel Order
                    </button>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Orders;
