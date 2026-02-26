import type { ReactNode } from "react";
import { Link } from "react-router-dom";

export default function NavLink({ children, path } : { children: ReactNode, path: string }) {
    return (
        <li>
            <Link to={path} className="navbar-link">
                {children}
            </Link>
        </li>
    )
}