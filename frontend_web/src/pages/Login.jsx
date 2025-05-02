import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { auth, googleProvider, signInWithPopup } from "../firebase";
import "../styles/login.css";
import logo from "../assets/Logo1.png";

export default function Login() {
  const [formData, setFormData] = useState({ email: "", password: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    const unsubscribe = auth.onAuthStateChanged((currentUser) => {
      if (currentUser) {
        navigate("/dashboard"); // Consistent casing
      }
    });
    return () => unsubscribe();
  }, [navigate]);

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError("");

    if (!formData.email || !formData.password) {
      setError("All fields are required.");
      setLoading(false);
      return;
    }

    try {
      const res = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: formData.email,
          password: formData.password,
        }),
      });

      const data = await res.json();
      if (!res.ok) throw new Error(data.message || "Login failed");

      localStorage.setItem("token", data.token);
      localStorage.setItem("role", data.role);
      localStorage.setItem("firstName", data.firstName);
      console.log(data); // Debug: Check the response data
      alert("Login successful!");

      if(data.role === "ADMIN") {
        navigate("/admin-dashboard");
      } else if(data.role === "USER") {
        navigate("/dashboard");
      } else {
        setError("Invalid role");
      }
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    setLoading(true);
    setError("");
    try {
      console.log("Starting Google Sign-In"); // Debug: Step 1
      const result = await signInWithPopup(auth, googleProvider);
      const user = result.user;
      const idToken = await user.getIdToken();
  
      console.log("Firebase ID Token obtained:", idToken.slice(0, 20) + "..."); // Debug: Step 2
  
      const response = await fetch("http://localhost:8080/auth/firebase-login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ idToken }),
      });
  
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || "Google login failed");
  
      console.log("Backend response:", data); // Debug: Step 3
      localStorage.setItem("token", data.token);
      console.log("Token stored in localStorage:", localStorage.getItem("token").slice(0, 20) + "..."); // Debug: Step 4
  
      console.log("Navigating to /dashboard"); // Debug: Step 5
      navigate("/dashboard");
      console.log("Navigation called"); // Debug: Step 6 (should appear if navigate doesn't block)
    } catch (err) {
      console.error("Google login error:", err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <div className="left-section">
        <div className="background-overlay"></div>
        <div className="logo-container">
          <img src={logo} alt="Logo" className="loginsidelogo" />
        </div>
      </div>

      <div className="right-section">
        <div className="form-container">
          <h2>WELCOME BACK!</h2>
          <p>Please enter your details.</p>

          {error && <p className="error-message">{error}</p>}

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="text"
              name="email"
              placeholder="Enter your email"
              value={formData.email}
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
              <a href="/forgot-password" className="forgot-password">
                Forgot password?
              </a>
            </div>
            <button type="submit" className="btn" disabled={loading}>
              {loading ? "Signing in..." : "Sign in"}
            </button>
          </form>

          <div className="google-signin">
            <button
              onClick={handleGoogleSignIn}
              className="btn google-btn"
              disabled={loading}
            >
              {loading ? "Signing in..." : "Sign in with Google"}
            </button>
          </div>

          <p>
            Don't have an account? <a href="/signup">Sign up for free!</a>
          </p>
        </div>
      </div>
    </div>
  );
}