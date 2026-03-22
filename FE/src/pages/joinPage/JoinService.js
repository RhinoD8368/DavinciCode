import api from '../../utils/axios';

export const JoinService = {

    /** @description 회원가입 API 호출 */
    signUp(signData) {
        return api.post('/api/auth/signUp', signData);
    }
    
};