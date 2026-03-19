import styles from './Login.module.css';
import Button from '../../components/button/Button';
import Input from '../../components/Input/Input';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';
import { useNavigate } from 'react-router-dom';
import api from '../../utils/axios';

const Login = () => {
    const navigate = useNavigate();

    /** @description Login 버튼 클릭 시 로그인 처리 */
    const handleLoginClick = () => {     
        api.get('/test')
            .then(response => {
                console.log('Login successful:', response.data);
                // 로그인 성공 시 처리 (예: 토큰 저장, 사용자 정보 저장 등)
            })
            .catch(error => {
                console.error('Login failed:', error);
                // 로그인 실패 시 처리 (예: 에러 메시지 표시 등)
            });
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