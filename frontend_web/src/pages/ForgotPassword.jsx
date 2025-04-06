import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "../styles/login.css";
import logo from "../assets/Logo1.png";

export default function ForgotPassword() {
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const handleSubmit = (e) => {
    e.preventDefault();

    // Add your email validation + backend call here
    alert("Password reset link sent to: " + email);
    navigate("/"); // Or wherever you want to redirect after
  };

  return (
    <div className="login-container">
      <div className="left-section">
        <div className="background-overlay"></div>
        <div className="logo-container">
          <img src={logo} alt="Logo" className="logo" />
        </div>
      </div>

      <div className="right-section">
        <div className="form-container">
          <h2>FORGOT PASSWORD?</h2>
          <p>Please enter your Email address.</p>

          <form onSubmit={handleSubmit} className="login-form">
            <input
              type="email"
              name="email"
              placeholder="Enter email address"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
            <button type="submit" className="btn">Submit</button>
          </form>

          <p>
            Already have an account? <a href="/login">Sign in here!</a>
          </p>
        </div>
      </div>
    </div>
  );
}
