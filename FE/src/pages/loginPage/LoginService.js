import api from '../../utils/axios';

export const LoginService = {
    
    /** @description 로그인 API 호출 */
    login(loginData) {
        return api.post('/api/auth/login', loginData);
    }
}