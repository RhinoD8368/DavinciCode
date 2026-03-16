import styles from './Login.module.css';
import Button from '../../components/button/Button';
import Input from '../../components/Input/Input';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';

const Login = () => {
    return (
        <div className={styles.loginContainer}>
            <VStack gap="10px" style={{ marginTop: "150px" }}>
                <Input label="ID" name="id" placeholder="아이디를 입력해주세요." />
                <Input label="PW" name="pw" placeholder="비밀번호를 입력해주세요." />
            </VStack>

            <VStack gap="25px" style={{ marginTop: "50px" }}>
                <Button>Login</Button>
                <Button>Join</Button>
            </VStack>
        </div>
    );
}

export default Login;