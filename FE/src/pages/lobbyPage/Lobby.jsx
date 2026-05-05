import styles from './Lobby.module.css';
import Button from '../../components/button/Button';
import { LobbyService } from './LobbyService';
import useAuthStore from '../../store/authStore';
import { errorUtil } from '../../utils/errorUtil';
import { useNavigate } from 'react-router-dom'; 

const Lobby = () => {

    const navigate = useNavigate();
    const setLogout = useAuthStore((state) => state.setLogout);
    
    const handleLogoutClick = async () => {
        try {
            console.log("logout clicked!!!");
            const result = await LobbyService.logout();
            setLogout();

            if(result.success){
                console.log(result);
                alert(result.message);

                // 뒤로가기 기록 남기지 않음
                navigate("/", {replace: true});
            }
        } catch (error) {
            errorUtil.errorProcess(error);
        }
    }

    return (
        <div>
            <h1>Lobby Page</h1>
            <p>Welcome to the lobby! This is where you can see available games and join them.</p>

            <Button onClick={handleLogoutClick}>Logout</Button>
        </div>
    );
};

export default Lobby;