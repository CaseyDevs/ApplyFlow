import { useAuth } from "../../context/AuthContext";
import { logoutUser } from "../../api/auth/logout";
import styles from "./Navbar.module.css";
import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useQueryClient } from "@tanstack/react-query";
import logo from "../../assets/applyflow_main.png";

export default function Navbar() {
    const navigate = useNavigate();
    const location = useLocation();
    const queryClient = useQueryClient();
    const { isLoggedIn, refreshUser } = useAuth();

    const handleLogout = async () => {
        try {
            await logoutUser();
            await refreshUser();
            queryClient.clear(); // clear cache
            navigate("/");
        } catch (err) {
            console.error("Logout failed:", err);
        }
    };

    const isActive = (path: string) => location.pathname === path;

    return (
        <nav className={styles.navbar}>
            <div className={styles.container}>
                <div className={styles.brand}>
                    <Link to="/" className={styles.logo}>
                        <img src={logo} alt="ApplyFlow" className={styles.logoImage} />
                    </Link>
                </div>
                <ul className={styles.menu}>
                    <li className={styles.menuItem}>
                        <Link to="/" className={`${styles.menuLink} ${isActive('/') ? styles.active : ''}`}>
                            Home
                        </Link>
                    </li>
                    <li className={styles.menuItem}>
                        <Link to="/about" className={`${styles.menuLink} ${isActive('/about') ? styles.active : ''}`}>
                            About
                        </Link>
                    </li>
                    {isLoggedIn ? (
                        <>
                            <li className={styles.menuItem}>
                                <Link to="/applications" className={`${styles.menuLink} ${isActive('/applications') ? styles.active : ''}`}>
                                    Applications
                                </Link>
                            </li>
                            <li className={styles.menuItem}>
                                <Link to="/job-boards" className={`${styles.menuLink} ${isActive('/job-boards') ? styles.active : ''}`}>
                                    Job Boards
                                </Link>
                            </li>
                            <li className={styles.menuItem}>
                                <button onClick={handleLogout} className={styles.logoutButton}>Logout</button>
                            </li>
                        </>
                    ) : (
                        <li className={styles.menuItem}>
                            <button onClick={() => navigate('/login')} className={styles.button}>
                                Sign In
                            </button>
                        </li>
                    )}
                </ul>
            </div>
        </nav>
    )
}