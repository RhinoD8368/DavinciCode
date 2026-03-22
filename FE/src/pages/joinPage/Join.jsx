import styles from './Join.module.css';
import { useEffect, useState } from "react";
import { useNavigate } from 'react-router-dom';
import { VStack, HStack } from '../../components/layouts/stackLayout/Stack';
import Input from '../../components/Input/Input';
import Button from '../../components/button/Button';
import { errorUtil } from '../../utils/errorUtil';
import { JoinService } from './JoinService';

const Join = () => {
    
    const navigate = useNavigate();

    const [signData, setSignData] = useState({
        userId: '',
        password: '',
        passwordConfirm: '',
        email: '',
        nickname: ''
    });

    const handleChange = (e) => {
        const { name, value } = e.target;
        setSignData(prevData => ({ 
            ...prevData,
            [name]: value
        }));
    }

    /** @description 회원가입 버튼 클릭 시 처리 */
    const handleSignUpClick = async () => {
        try {
            const joinResult = await JoinService.signUp(signData);
            if( joinResult.success ) {
                alert(joinResult.message);
                navigate("/");
            }
        } catch (error) {
            errorUtil.errorProcess(error);
        }
    }

    return (
        <div>
            <VStack gap="20px" style={{ marginTop: "80px" }}>
                <Input label="ID" name="userId" placeholder="아이디를 입력해주세요." 
                    value={signData.userId} 
                    onChange={handleChange}
                />
                <Input label="Password" name="password" placeholder="비밀번호를 입력해주세요." 
                    value={signData.password} 
                    onChange={handleChange}
                />
                <Input label="Password Confirm" name="passwordConfirm" placeholder="비밀번호를 다시 입력해주세요." 
                    value={signData.passwordConfirm} 
                    onChange={handleChange}
                />
                <Input label="E-Mail" name="email" placeholder="이메일을 입력해주세요." 
                    value={signData.email} 
                    onChange={handleChange}
                />
                <Input label="Nickname" name="nickname" placeholder="닉네임을 입력해주세요." 
                    value={signData.nickname} 
                    onChange={handleChange}
                />
            </VStack>

            <VStack style={{ marginTop: "50px" }}>
                <Button onClick={handleSignUpClick}>Sign Up</Button>
            </VStack>
        </div>
    );
}

export default Join;