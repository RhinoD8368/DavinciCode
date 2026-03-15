import styles from './Login.module.css';
import Button from '../../components/button/Button';
import Input from '../../components/Input/Input';

const Login = () => {
    return (
        <div className={styles.loginContainer}>
            <Input id="loginId" name="loginId" label="ID" type="text" placeholder="ID" />
            <Input id="password" name="password" label="PW" type="password" placeholder="Password" />
            <Button>Login</Button>
            <Button>Join</Button>
        </div>
    );
}

export default Login;