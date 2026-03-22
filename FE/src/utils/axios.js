import axios from 'axios';

// npm run dev   실행 시: Vite가 자동으로 .env.development 읽어서 baseURL을 설정합니다.(개발환경)
// npm run build 실행 시: Vite가 자동으로 .env.production  읽어서 baseURL을 설정합니다.(운영환경)
const api = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL,  
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json',
    },
});

api.interceptors.response.use(
    (response) => {
        console.log("### [axios - interceptors] request :: ", response);
        return response.data;
    },
    async (error) => {
        console.error("### [axios - interceptors] request error :: ", error);
        return Promise.reject(error);
    }
);

export default api;