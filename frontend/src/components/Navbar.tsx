import { useAuth } from "../context/AuthContext";
import { logoutUser } from "../api/auth/logout";
import "./Navbar.css";

export default function Navbar() {
    const { user, refreshUser } = useAuth();

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
                {user ? (
                    <>
                        <li>
                            <span className="navbar-user">Hi, {user.username || user.email}</span>
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
                            <a href="#login" className="navbar-link">Login</a>
                        </li>
                        <li>
                            <a href="#register" className="navbar-link">Register</a>
                        </li>
                    </>
                )}
            </ul>
        </nav>
    );
}