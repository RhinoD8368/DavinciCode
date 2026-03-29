import api from '../../utils/axios';

export const LobbyService = {
    
    /** @description 로그아웃 API 호출 */
    logout() {
        return api.post('/api/auth/logout');
    }
}