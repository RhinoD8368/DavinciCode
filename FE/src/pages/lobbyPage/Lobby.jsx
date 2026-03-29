import styles from './Lobby.module.css';
import Button from '../../components/button/Button';
import { LobbyService } from './LobbyService';

const handleLogoutClick = async () => {
    try {
        const result = await LobbyService.logout();

        if(result.success){
            alert(result.message);

            // 뒤로가기 기록 남기지 않음
            navigate("/", {replace: true});
        }
    } catch (error) {
        errorUtil.errorProcess(error);
    }
}

const Lobby = () => {
    return (
        <div>
            <h1>Lobby Page</h1>
            <p>Welcome to the lobby! This is where you can see available games and join them.</p>

            <Button onClick={handleLogoutClick}>Logout</Button>
        </div>
    );
};

export default Lobby;