import { Link, useNavigate } from "react-router-dom";

const Navbar = () => {
  const navigate = useNavigate();

  const user = JSON.parse(localStorage.getItem("user"));
  const isAdmin = user?.role === "ADMIN";

  const handleLogout = () => {
    localStorage.clear();
    navigate("/login");
  };

  return (
    <nav className="navbar">
      <h2>Kasthuri Sweets</h2>

      <div>
        <Link to="/">Home</Link>
        <Link to="/products">Products</Link>

        {user && <Link to="/cart">Cart</Link>}
        {user && <Link to="/orders">Orders</Link>}

        {/* ✅ Correct Admin Links */}
        {isAdmin && (
          <>
            <Link to="/admin/dashboard">Dashboard</Link>
            <Link to="/admin/orders">Admin Orders</Link>
            <Link to="/admin/products">Admin Products</Link>
          </>
        )}

        {user ? (
          <button onClick={handleLogout}>Logout</button>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
