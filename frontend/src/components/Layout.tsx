import { NavLink, Outlet, useLocation } from "react-router-dom";

const nav = [
  { to: "/", label: "Dashboard", icon: "▦" },
  { to: "/patients", label: "Patients", icon: "♙" },
  { to: "/reports", label: "Reports", icon: "▤" },
  { to: "/test-master", label: "Test Master", icon: "⚗" },
];

export default function Layout() {
  const location = useLocation();
  const title = location.pathname.startsWith("/reports/create") ? "Create Report"
    : location.pathname.startsWith("/reports/")
      ? "Report Details"
      : (nav.find((item) => item.to === location.pathname)?.label ??
        "Pathology Laboratory");

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <div className="brand-mark">A</div>
          <div>
            <strong>Agrawal Pathology</strong>
            <span>Laboratory System</span>
          </div>
        </div>
        <nav className="nav">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === "/"}
              className={({ isActive }) =>
                isActive ? "nav-link active" : "nav-link"
              }
            >
              <span className="nav-icon">{item.icon}</span>
              {item.label}
            </NavLink>
          ))}
        </nav>
        <div className="sidebar-footer">
          <span className="online-dot" /> System online
          <small>Spring Boot + React</small>
        </div>
      </aside>
      <div className="main-shell">
        <header className="topbar">
          <div>
            <div className="breadcrumb">
              Laboratory <span>/</span> {title}
            </div>
            <h1>{title}</h1>
          </div>
          <div className="user-pill">
            <span className="avatar">A</span>
            <span>Administrator</span>
          </div>
        </header>
        <main className="content">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
