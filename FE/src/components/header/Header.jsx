import styles from "./Header.module.css";
import { useNavigate } from "react-router-dom";

const Header = () => {
    const navigate = useNavigate();
    const handleOnClick = () => {
        navigate("/");
    }

    return (
        <div className={styles.header} onClick={handleOnClick}>
            Tomato's &nbsp; Davinch-Code
        </div>
    );
}

export default Header;