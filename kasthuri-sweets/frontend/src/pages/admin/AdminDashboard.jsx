import { useEffect, useState } from "react";
import API from "../../api/axios";

const AdminDashboard = () => {
  const [orders, setOrders] = useState([]);

  useEffect(() => {
    API.get("/orders/admin/all")
      .then((res) => setOrders(res.data))
      .catch((err) => console.log(err));
  }, []);

  const totalRevenue = orders.reduce(
    (sum, order) => sum + (order.totalAmount || 0),
    0
  );

  return (
    <div className="admin-container">
      <h2>Admin Dashboard</h2>

      <div className="dashboard-grid">
        <div className="card">
          <h3>Total Orders</h3>
          <p>{orders.length}</p>
        </div>

        <div className="card">
          <h3>Total Revenue</h3>
          <p>₹{totalRevenue}</p>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
