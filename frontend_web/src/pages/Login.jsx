import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/login.css";
import logo from "../assets/Logo1.png";

export default function Login() {
  const [formData, setFormData] = useState({ username: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (!formData.username || !formData.password) {
      setError("All fields are required.");
      setLoading(false);
      return;
    }

    try {
      const res = await fetch("http://localhost:5000/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(formData),
      });

      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Login failed");

      alert("Login successful!");
      navigate("/dashboard");
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      {/* Left Section - Logo & Background */}
      <div className="left-section">
        <div className="background-overlay"></div>
        <div className="logo-container">
          <img src={logo} alt="Logo" className="logo" />
        </div>
      </div>

      {/* Right Section - Login Form */}
      <div className="right-section">
        <div className="form-container">
          <h2>WELCOME BACK!</h2>
          <p>Please enter your details.</p>

          {error && <p className="error-message">{error}</p>}

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="text"
              name="username"
              placeholder="Enter your username"
              value={formData.username}
              onChange={handleChange}
              required
            />
            <input
              type="password"
              name="password"
              placeholder="Enter your password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <div className="options">
              <label>
                <input type="checkbox" name="remember" /> Remember
              </label>
              <a href="/forgot-password" className="forgot-password">Forgot password?</a>
              </div>
            <button type="submit" className="btn" disabled={loading}>
              {loading ? "Signing in..." : "Sign in"}
            </button>
          </form>

          <p>
            Don't have an account? <a href="/signup">Sign up for free!</a>
          </p>
        </div>
      </div>
    </div>
  );
}
