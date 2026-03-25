import { useAuth } from "../../context/AuthContext";
import { logoutUser } from "../../api/auth/logout";
import "./Navbar.css";
import { Link, useNavigate } from 'react-router-dom';
import NavLink from "../NavLink";
import { useQueryClient } from "@tanstack/react-query";

export default function Navbar() {
    const navigate = useNavigate();
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

    return (
        <nav className="navbar">
            <div className="navbar-brand">
                <h1><Link to="/" id="nav-logo">ApplyFlow</Link></h1>
            </div>
            <ul className="navbar-menu">
                {isLoggedIn ? (
                    <>
                        <NavLink path="/applications">Applications</NavLink>
                        <NavLink path="/job-boards">Job Boards</NavLink>
                        <NavLink path="/">About</NavLink>
                        <li><button onClick={handleLogout} className="navbar-button">Logout</button></li>
                    </>
                ) : (
                    <>
                        <NavLink path="/login">Login</NavLink>
                    </>
                )}
            </ul>
        </nav>
    )
}