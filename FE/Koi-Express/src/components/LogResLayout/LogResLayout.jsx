import Header from "../Header/Header";
import "./LogResLayout.css"; // Import the CSS file with unique styles

export default function LoginLayout({ children }) {
  return (
    <div className="header-layout">
      <Header />
      <main className="header-layout-content">{children}</main>
    </div>
  );
}
