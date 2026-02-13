import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../api/axios";

const Checkout = () => {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    customerName: "",
    phoneNumber: "",
    address: "",
    city: "",
    pincode: "",
  });

  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value,
    });
  };

  const handlePlaceOrder = async () => {
    // 🔎 Basic Validation
    if (
      !formData.customerName ||
      !formData.phoneNumber ||
      !formData.address ||
      !formData.city ||
      !formData.pincode
    ) {
      setMessage("❌ Please fill all delivery details.");
      return;
    }

    setLoading(true);
    setMessage("");

    try {
      await API.post("/orders/place", formData);

      setMessage("✅ Order placed successfully!");
      setTimeout(() => {
        navigate("/orders");
      }, 1500);

    } catch (error) {
      console.error(error);
      setMessage("❌ Failed to place order.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="checkout-container">
      <h2>Delivery Details</h2>

      {message && (
        <p style={{ marginBottom: "15px", fontWeight: "500" }}>
          {message}
        </p>
      )}

      <div className="checkout-form">

        <input
          type="text"
          name="customerName"
          placeholder="Full Name"
          value={formData.customerName}
          onChange={handleChange}
        />

        <input
          type="text"
          name="phoneNumber"
          placeholder="Phone Number"
          value={formData.phoneNumber}
          onChange={handleChange}
        />

        <textarea
          name="address"
          placeholder="Full Address"
          value={formData.address}
          onChange={handleChange}
        />

        <input
          type="text"
          name="city"
          placeholder="City"
          value={formData.city}
          onChange={handleChange}
        />

        <input
          type="text"
          name="pincode"
          placeholder="Pincode"
          value={formData.pincode}
          onChange={handleChange}
        />

        <button
          className="btn-primary"
          onClick={handlePlaceOrder}
          disabled={loading}
        >
          {loading ? "Processing..." : "Confirm Order"}
        </button>

        <button
          className="btn-secondary"
          onClick={() => navigate("/cart")}
          disabled={loading}
        >
          Back to Cart
        </button>
      </div>
    </div>
  );
};

export default Checkout;
