import styles from './Login.module.css';
import Button from '../../components/button/Button';
import Input from '../../components/Input/Input';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';
import { useNavigate } from 'react-router-dom'; 
import { LoginService } from './LoginService';
import { useState } from 'react';
import { errorUtil } from '../../utils/errorUtil';
import useAuthStore from '../../store/authStore';

const Login = () => {
    const navigate = useNavigate();

    const setLogin = useAuthStore((state) => state.setLogin);

    const [loginData, setLoginData] = useState({
        userId: '',
        password: ''
    });

    /** @description 입력 필드 값 변경 시 호출 */
    const handleChange = (e) => {
        const { name, value } = e.target;
        setLoginData(prevData => ({ 
            ...prevData,
            [name]: value
        }));
    }

    /** @description Join 버튼 클릭 시 회원가입 페이지로 이동 */
    const handleJoinClick = () => {
        navigate('/join');
    }

    /** @description Login 버튼 클릭 시 로그인 처리 */
    const handleLoginClick = async () => {    
        try {
            const loginResult = await LoginService.login(loginData);

            if( loginResult.success ) {
                const { accessToken, userDTO } = loginResult.data;
                setLogin(accessToken, userDTO);

                navigate("/lobby");
            }
        } catch (error) {
            errorUtil.errorProcess(error);
        }
    }
    
    
    return (
        <div className={styles.loginContainer}>
            <VStack gap="10px" style={{ marginTop: "150px" }}>
                <Input label="ID" name="userId" placeholder="아이디를 입력해주세요." 
                    value={loginData.userId}
                    onChange={handleChange}
                />
                <Input label="PW" name="password" placeholder="비밀번호를 입력해주세요." 
                    value={loginData.password}
                    onChange={handleChange}
                />
            </VStack>

            <VStack gap="25px" style={{ marginTop: "50px" }}>
                <Button onClick={handleLoginClick}>Login</Button>
                <Button onClick={handleJoinClick}>Join</Button>
            </VStack>
        </div>
    );
}

export default Login;