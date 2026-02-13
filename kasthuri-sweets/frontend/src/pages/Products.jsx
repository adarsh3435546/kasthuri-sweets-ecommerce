import { useEffect, useState } from "react";
import API from "../api/axios";

const Products = () => {
  const [products, setProducts] = useState([]);
  const [quantities, setQuantities] = useState({});
  const [message, setMessage] = useState("");

  useEffect(() => {
    API.get("/products")
      .then(res => setProducts(res.data))
      .catch(err => console.error(err));
  }, []);

  const handleQuantityChange = (id, value) => {
    setQuantities({ ...quantities, [id]: value });
  };

  const handleAddToCart = async (productId) => {
    const quantity = quantities[productId] || 1;

    try {
      await API.post("/cart/add", {
        productId,
        quantity,
      });
      setMessage("Added to cart successfully!");
      setTimeout(() => setMessage(""), 2000);
    } catch (error) {
      setMessage("Please login first.");
      setTimeout(() => setMessage(""), 2000);
    }
  };

  return (
    <div>
      <h2>Our Sweets</h2>

      {message && <p style={{ color: "green" }}>{message}</p>}

      <div className="products-grid">
        {products.map(product => (
          <div key={product.id} className="product-card">

            {/* ✅ PRODUCT IMAGE */}
            {product.imageUrl && (
              <img
                src={`http://localhost:8080${product.imageUrl}`}
                alt={product.name}
                style={{
                  width: "100%",
                  height: "200px",
                  objectFit: "cover",
                  borderRadius: "8px",
                  marginBottom: "10px"
                }}
              />
            )}

            <h3>{product.name}</h3>
            <p>{product.description}</p>
            <p className="price">₹{product.price}</p>
            <p className="stock">
              {product.quantity > 0
                ? `In Stock: ${product.quantity}`
                : "Out of Stock"}
            </p>

            {product.quantity > 0 ? (
              <>
                <input
                  type="number"
                  min="1"
                  max={product.quantity}
                  value={quantities[product.id] || 1}
                  onChange={(e) =>
                    handleQuantityChange(product.id, e.target.value)
                  }
                  style={{ width: "60px", marginRight: "10px" }}
                />
                <button
                  className="btn-primary"
                  onClick={() => handleAddToCart(product.id)}
                >
                  Add to Cart
                </button>
              </>
            ) : (
              <button disabled className="btn-danger">
                Out of Stock
              </button>
            )}

          </div>
        ))}
      </div>
    </div>
  );
};

export default Products;
