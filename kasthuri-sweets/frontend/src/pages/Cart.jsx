import { useEffect, useState } from "react";
import API from "../api/axios";
import { useNavigate } from "react-router-dom";

const Cart = () => {
  const [cart, setCart] = useState([]);
  const [total, setTotal] = useState(0);

  // 🔥 DELIVERY DETAILS STATE
  const [customerName, setCustomerName] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [address, setAddress] = useState("");
  const [city, setCity] = useState("");
  const [pincode, setPincode] = useState("");

  const navigate = useNavigate();

  // ================= FETCH CART =================
  const fetchCart = async () => {
    try {
      const res = await API.get("/cart");
      setCart(res.data);

      const totalAmount = res.data.reduce(
        (sum, item) => sum + item.price * item.quantity,
        0
      );
      setTotal(totalAmount);

    } catch (error) {
      console.error("Error fetching cart:", error);
    }
  };

  useEffect(() => {
    fetchCart();
  }, []);

  // ================= REMOVE ITEM =================
  const handleRemove = async (productId) => {
    try {
      await API.delete(`/cart/${productId}`);
      fetchCart();
    } catch (error) {
      console.error("Remove failed:", error);
    }
  };

  // ================= PLACE ORDER =================
  const handlePlaceOrder = async () => {
    try {

      if (!customerName || !phoneNumber || !address || !city || !pincode) {
        alert("Please fill all delivery details");
        return;
      }

      await API.post("/orders/place", {
        customerName,
        phoneNumber,
        address,
        city,
        pincode
      });

      alert("Order placed successfully!");

      // Clear form
      setCustomerName("");
      setPhoneNumber("");
      setAddress("");
      setCity("");
      setPincode("");

      fetchCart();
      navigate("/orders");

    } catch (error) {
      console.error(error.response?.data || error.message);
      alert("Failed to place order");
    }
  };

  return (
    <div>
      <h2>Your Cart</h2>

      {cart.length === 0 ? (
        <p>Your cart is empty</p>
      ) : (
        <>
          <div className="products-grid">
            {cart.map(item => (
              <div key={item.productId} className="product-card">

                {/* ✅ PRODUCT IMAGE */}
                {item.imageUrl && (
                  <img
                    src={`http://localhost:8080${item.imageUrl}`}
                    alt={item.productName}
                    style={{
                      width: "150px",
                      height: "150px",
                      objectFit: "cover",
                      borderRadius: "8px",
                      marginBottom: "10px"
                    }}
                  />
                )}

                <h3>{item.productName}</h3>
                <p>Price: ₹{item.price}</p>
                <p>Quantity: {item.quantity}</p>
                <p className="price">
                  Subtotal: ₹{item.price * item.quantity}
                </p>

                <button
                  className="btn-danger"
                  onClick={() => handleRemove(item.productId)}
                >
                  Remove
                </button>
              </div>
            ))}
          </div>

          {/* 🔥 DELIVERY FORM */}
          <div className="summary-box">
            <h3>Total: ₹{total}</h3>

            <h4>Delivery Details</h4>

            <input
              type="text"
              placeholder="Customer Name"
              value={customerName}
              onChange={(e) => setCustomerName(e.target.value)}
            />

            <input
              type="text"
              placeholder="Phone Number"
              value={phoneNumber}
              onChange={(e) => setPhoneNumber(e.target.value)}
            />

            <input
              type="text"
              placeholder="Address"
              value={address}
              onChange={(e) => setAddress(e.target.value)}
            />

            <input
              type="text"
              placeholder="City"
              value={city}
              onChange={(e) => setCity(e.target.value)}
            />

            <input
              type="text"
              placeholder="Pincode"
              value={pincode}
              onChange={(e) => setPincode(e.target.value)}
            />

            <button
              className="btn-primary"
              onClick={handlePlaceOrder}
            >
              Place Order
            </button>
          </div>
        </>
      )}
    </div>
  );
};

export default Cart;
