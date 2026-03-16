import styles from './Login.module.css';
import Button from '../../components/button/Button';
import Input from '../../components/Input/Input';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';
import { useNavigate } from 'react-router-dom';

const Login = () => {
    const navigate = useNavigate();

    /** @description Login 버튼 클릭 시 로그인 처리 */
    const handleLoginClick = () => {     
        alert("로그인 버튼 클릭");
    }
    
    /** @description Join 버튼 클릭 시 회원가입 페이지로 이동 */
    const handleJoinClick = () => {
        navigate('/join');
    }

    return (
        <div className={styles.loginContainer}>
            <VStack gap="10px" style={{ marginTop: "150px" }}>
                <Input label="ID" name="id" placeholder="아이디를 입력해주세요." />
                <Input label="PW" name="pw" placeholder="비밀번호를 입력해주세요." />
            </VStack>

            <VStack gap="25px" style={{ marginTop: "50px" }}>
                <Button onClick={handleLoginClick}>Login</Button>
                <Button onClick={handleJoinClick}>Join</Button>
            </VStack>
        </div>
    );
}

export default Login;