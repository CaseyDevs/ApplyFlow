import { useAuth } from "../context/AuthContext";
import { logoutUser } from "../api/auth/logout";
import "./Navbar.css";
import { Link } from 'react-router-dom';

export default function Navbar() {
    const { user, isLoggedIn, refreshUser } = useAuth();

    const handleLogout = async () => {
        try {
            await logoutUser();
            await refreshUser();
        } catch (err) {
            console.error("Logout failed:", err);
        }
    };

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <h1>ApplyFlow</h1>
            </div>
            <ul className="navbar-menu">
                {isLoggedIn && user ? (
                    <>
                        <li>
                            <span className="navbar-user">Hi, {user.email}</span>
                        </li>
                        <li>
                            <Link to="/applications" className="navbar-link">Applications</Link>
                        </li>
                        <li>
                            <button onClick={handleLogout} className="navbar-button">
                                Logout
                            </button>
                        </li>
                    </>
                ) : (
                    <>
                        <li>
                            <Link to="/login" className="navbar-link">Login</Link>
                        </li>
                        <li>
                            <Link to="/register" className="navbar-link">Register</Link>
                        </li>
                    </>
                )}
            </ul>
        </nav>
    )
}