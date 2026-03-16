import styles from './Join.module.css';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';
import Input from '../../components/Input/Input';
import Button from '../../components/button/Button';

const Join = () => {

    /** @description 회원가입 버튼 클릭 시 처리 */
    const handleSignUpClick = () => {
        alert("회원가입 버튼 클릭");
    }

    return (
        <div>
            <VStack gap="20px" style={{ marginTop: "50px" }}>
                <Input label="ID" name="id" placeholder="아이디를 입력해주세요." />
                <Input label="Password" name="pw" placeholder="비밀번호를 입력해주세요." />
                <Input label="Password Confirm" name="pwConfirm" placeholder="비밀번호를 다시 입력해주세요." />
                <Input label="E-Mail" name="email" placeholder="이메일을 입력해주세요." />
                <Input label="Nickname" name="nickname" placeholder="닉네임을 입력해주세요." />
            </VStack>

            <VStack style={{ marginTop: "50px" }}>
                <Button onClick={handleSignUpClick}>Sign Up</Button>
            </VStack>
        </div>
    );
}

export default Join;