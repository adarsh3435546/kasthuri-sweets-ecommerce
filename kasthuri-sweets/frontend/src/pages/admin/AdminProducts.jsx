import { useEffect, useState } from "react";
import API from "../../api/axios";
import "./admin.css";

const AdminProducts = () => {
  const [products, setProducts] = useState([]);

  const [form, setForm] = useState({
    name: "",
    description: "",
    price: "",
    quantity: "",
  });

  const [image, setImage] = useState(null);
  const [editingId, setEditingId] = useState(null);

  // ===============================
  // Fetch Products
  // ===============================
  const fetchProducts = async () => {
    try {
      const res = await API.get("/products");
      setProducts(res.data);
    } catch (err) {
      console.error(err);
    }
  };

  useEffect(() => {
    fetchProducts();
  }, []);

  // ===============================
  // Handle Input Change
  // ===============================
  const handleChange = (e) => {
    setForm({ ...form, [e.target.name]: e.target.value });
  };

  // ===============================
  // Handle Submit (ADD + UPDATE)
  // ===============================
  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const formData = new FormData();

      formData.append("name", form.name);
      formData.append("description", form.description);
      formData.append("price", form.price);
      formData.append("quantity", form.quantity);

      if (image) {
        formData.append("image", image);
      }

      if (editingId) {
        // UPDATE PRODUCT (with optional image)
        await API.put(`/products/${editingId}`, formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
        setEditingId(null);
      } else {
        // ADD PRODUCT
        await API.post("/products", formData, {
          headers: { "Content-Type": "multipart/form-data" },
        });
      }

      // Reset form
      setForm({
        name: "",
        description: "",
        price: "",
        quantity: "",
      });

      setImage(null);
      fetchProducts();

    } catch (err) {
      console.error(err);
      alert("Operation failed");
    }
  };

  // ===============================
  // Edit Product
  // ===============================
  const handleEdit = (product) => {
    setEditingId(product.id);
    setForm({
      name: product.name,
      description: product.description || "",
      price: product.price,
      quantity: product.quantity,
    });
  };

  // ===============================
  // Delete Product
  // ===============================
  const handleDelete = async (id) => {
    if (!window.confirm("Are you sure you want to delete this product?"))
      return;

    try {
      await API.delete(`/products/${id}`);
      fetchProducts();
    } catch (err) {
      console.error(err);
      alert("Delete failed");
    }
  };

  return (
    <div>
      <h2>Manage Products</h2>

      {/* ================= Form ================= */}
      <form className="admin-form" onSubmit={handleSubmit}>
        <input
          type="text"
          name="name"
          placeholder="Product Name"
          value={form.name}
          onChange={handleChange}
          required
        />

        <input
          type="text"
          name="description"
          placeholder="Description"
          value={form.description}
          onChange={handleChange}
          required
        />

        <input
          type="number"
          name="price"
          placeholder="Price"
          value={form.price}
          onChange={handleChange}
          required
        />

        <input
          type="number"
          name="quantity"
          placeholder="Stock Quantity"
          value={form.quantity}
          onChange={handleChange}
          required
        />

        {/* Image Upload */}
        <input
          type="file"
          accept="image/*"
          onChange={(e) => setImage(e.target.files[0])}
        />

        <button type="submit">
          {editingId ? "Update Product" : "Add Product"}
        </button>
      </form>

      {/* ================= Product List ================= */}
      <div className="admin-products-grid">
        {products.map((product) => (
          <div key={product.id} className="admin-product-card">
            <h3>{product.name}</h3>

            {product.imageUrl && (
              <img
                src={`http://localhost:8080${product.imageUrl}`}
                alt={product.name}
                width="120"
              />
            )}

            <p>{product.description}</p>
            <p>Price: ₹{product.price}</p>
            <p>Stock: {product.quantity}</p>

            <div className="admin-product-actions">
              <button onClick={() => handleEdit(product)}>
                Edit
              </button>

              <button
                className="delete-btn"
                onClick={() => handleDelete(product.id)}
              >
                Delete
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default AdminProducts;
